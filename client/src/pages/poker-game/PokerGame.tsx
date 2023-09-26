import React, {useEffect, useState} from "react";

import {Button, Container, Flex, Group, Paper, Stack, Title} from "@mantine/core";
import {useNavigate, useParams} from "react-router-dom";
import {useServices} from "../../hooks/service-provider/ServiceProvider";
import {PokerGameState} from "../../models/PokerGameState";
import {ContentLoader} from "../../components/loader/ContentLoader";
import {useUser} from "../../hooks/user-provider/UserProvider";
import {ActionModal} from "../../components/action-modal/ActionModal";
import {useDisclosure, useViewportSize} from "@mantine/hooks";
import {PokerTable} from "../../components/poker-table/PokerTable";

export const PokerGame: React.FunctionComponent = () => {
    const { id } = useParams();
    const services = useServices();
    const navigate = useNavigate();
    const [gameState, setGameState] = useState<PokerGameState | null>(null);
    const user = useUser().user;
    const [opened, { open, close }] = useDisclosure(false);
    const view = useViewportSize();
    const tableHeight = view.height - 150;

    const fetchGameState = async () => {
        try {
            const response: PokerGameState = await services.pokerService.getPokerGameState(id!);
            setGameState(response);
        } catch (error) {
            // TODO error toast
            navigate('/lobby');
            console.error('Game not found:', error);
        }
    };

    const startGame = async () => {
        try {
            await services.pokerService.startGame(id!);
        } catch (error) {
            // TODO error toast
            console.error('Error starting game:', error);
        }
    }

    const updateGameState = (gameState: PokerGameState) => {
        setGameState((prevState) => {
            return { ...prevState, ...gameState };
        });
    }

    const leaveGame = async () => {
        try {
            await services.pokerService.leave(id!);
            navigate('/lobby');
        } catch (error) {
            // TODO error toast
            console.error('Error leaving game:', error);
        }
    }

    useEffect(() => {
        services.webSocketService.subscribe(`/topic/game/${id}`, (message) => {
            updateGameState(message);
        });

        return () => {
            services.webSocketService.unsubscribe(`/topic/game/${id}`);
        };
    }, [services.webSocketService]);

    useEffect(() => {
        fetchGameState();
    }, []);

    return (
        gameState ?
            (
                <>
                    <Container fluid style={{padding: 'xs', height: 50}}>
                        <Paper shadow="md" style={{ width:'100%', display: 'flex',  padding: 10 }}>
                            <Flex
                                gap="lg"
                                justify="flex-end"
                                align="center"
                                direction="row"
                                wrap="wrap"
                                style={{ width: '100%' }}
                            >
                                <Button disabled={!gameState || gameState && gameState.gameStatus !== 'Waiting for Players'}
                                        onClick={startGame} size="sm" color="cyan">Start Game</Button>
                                <Button onClick={open} size="sm" color="red">Leave Game</Button>
                            </Flex>

                        </Paper>
                    </Container>
                    <Container fluid style={{height: tableHeight}}>
                        <PokerTable game={gameState}/>
                    </Container>
                    {opened &&<ActionModal opened={opened} onConfirm={leaveGame} close={close} title="LEAVE GAME"
                                           message="Do you really want to leave the game?"></ActionModal>}
                </>
            )
            : (<ContentLoader text={"Loading Game..."}></ContentLoader>)
    )
}