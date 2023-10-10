import React, {useEffect, useState} from "react";

import {Button, Container, Flex, Paper} from "@mantine/core";
import {useNavigate, useParams} from "react-router-dom";
import {useServices} from "../../hooks/service-provider/ServiceProvider";
import {PokerGameState} from "../../models/PokerGameState";
import {ContentLoader} from "../../components/loader/ContentLoader";
import {useUser} from "../../hooks/user-provider/UserProvider";
import {ActionModal} from "../../components/action-modal/ActionModal";
import {useDisclosure, useViewportSize} from "@mantine/hooks";
import {PokerTable} from "../../components/poker-table/PokerTable";
import {PokerPlayerDto} from "../../models/PokerPlayerDto";

export const PokerGame: React.FunctionComponent = () => {
    const { id } = useParams();
    const services = useServices();
    const navigate = useNavigate();
    const [gameState, setGameState] = useState<PokerGameState | null>(null);
    const [playerState, setPlayerState] = useState<PokerPlayerDto | null>(null);
    const user = useUser().user;
    const [opened, { open, close }] = useDisclosure(false);
    const view = useViewportSize();
    const tableHeight = view.height - 150;

    const fetchGameState = async () => {
        try {
            const response: PokerGameState = await services.pokerService.getPokerGameState(id!);
            setGameState(response);
        } catch (error) {
            navigate('/lobby');
        }
    };

    const startGame = async () => {
        await services.pokerService.startGame(id!);
    }

    const updateGameState = (gameState: PokerGameState) => {
        setGameState((prevState) => {
            return { ...prevState, ...gameState };
        });
    }

    const updatePlayerState = (playerState: PokerPlayerDto) => {
        setPlayerState((prevState) => {
            return { ...prevState, ...playerState };
        });
    }

    const handleShowdown = (state: PokerGameState) => {
        console.log('handle showdown called')
        if (state == null || state.showdownOrder == null) {
            console.log('gameState or showdownOrder is null')
            return;
        }

        let currentIndex = 0;

        const interval = setInterval(() => {
            if (currentIndex >= state.showdownOrder.length) {
                clearInterval(interval);
                return;
            }

            const currentPlayer = state.showdownOrder[currentIndex];
            const seatIndex = state.players.findIndex((p) => p.name === currentPlayer.name);
            console.log(currentPlayer)
            if (seatIndex !== -1) {
                const updatedPlayers = [...state.players];
                console.log('updatedPlayers', updatedPlayers)
                updatedPlayers[seatIndex] = currentPlayer;

                const updatedGameState = {
                    ...state,
                    players: updatedPlayers,
                };
                console.log(updatedGameState)
                updateGameState(updatedGameState);

                currentIndex++;
            }
        }, 2000); // 2-second interval
    };

    const leaveGame = async () => {
        try {
            await services.pokerService.leave(id!);
            navigate('/lobby');
        } catch (error) {
            console.error('Error leaving game:', error);
        }
    }

    useEffect(() => {
        services.webSocketService.subscribe(`/topic/game/${id}`, (message) => {
            console.log('game state', message)
            if (message.operation && message.operation === 'SHOWDOWN') {
                console.log('handle showdown')
                handleShowdown(message);
                return;
            }
            updateGameState(message);
        });

        services.webSocketService.subscribe(`/user/queue/${id}/private-info`, (message) => {
            console.log('private info', message)
            updatePlayerState(message);
        });

        return () => {
            services.webSocketService.unsubscribe(`/topic/game/${id}`);
            services.webSocketService.unsubscribe(`/user/queue/${id}/private-info`);
        };
    }, [services.webSocketService]);

    useEffect(() => {
        fetchGameState();
    }, []);

    return (
        gameState ?
            (
                <>
                    <Container fluid style={{padding:0, height: 50}}>
                        <Paper shadow="md" style={{ width:'100%', display: 'flex',  padding: 10 }}>
                            <Flex
                                gap="lg"
                                justify="space-between"
                                align="center"
                                direction="row"
                                wrap="wrap"
                                style={{ width: '100%' }}
                            >
                                {gameState &&
                                    (<span style={{ flex: 1, fontWeight: 'bold', fontSize: '1.5em' }}>
                                        {gameState.gameStatus.toUpperCase()} - PLAYERS: ({gameState.players.length}/{gameState.maxPlayers})
                                    </span>)}
                                <Button disabled={!gameState || gameState && ( gameState.gameStatus !== 'Waiting for Players'
                                    || gameState.players.length < 2 || gameState.players[0].name !== user!.username)}
                                        onClick={startGame} size="sm" color="cyan">Start Game</Button>
                                <Button onClick={open} size="sm" color="red">Leave Game</Button>
                            </Flex>

                        </Paper>
                    </Container>
                    <Container fluid style={{height: tableHeight}}>
                        <PokerTable player={playerState} game={gameState}/>

                    </Container>
                    {opened &&<ActionModal opened={opened} onConfirm={leaveGame} close={close} title="LEAVE GAME"
                                           message="Do you really want to leave the game?"></ActionModal>}
                </>
            )
            : (<ContentLoader text={"Loading Game..."}></ContentLoader>)
    )
}