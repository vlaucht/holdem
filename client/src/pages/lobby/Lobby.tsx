import React, {useEffect, useState} from "react";
import {GameList} from "../../components/lobby/game-list/GameList";
import {PokerGameLobbyDto} from "../../models/PokerGameLobbyDto";
import {useDisclosure} from "@mantine/hooks";
import {Button, Container, Flex} from "@mantine/core";
import {IconPlus} from "@tabler/icons-react";
import {CreateGameModal} from "../../components/lobby/create-modal/CreateGameModal";
import {useServices} from "../../hooks/service-provider/ServiceProvider";


export const Lobby: React.FunctionComponent = () => {
    const services = useServices();
    const [pokerGames, setPokerGames] = useState<PokerGameLobbyDto[]>([]);
    const [opened, { open, close }] = useDisclosure(false);

    const fetchData = async () => {
        try {
            const response: PokerGameLobbyDto[] = await services.lobbyService.getAll();
            setPokerGames(response);
        } catch (error) {
            // TODO toast
            console.error('Error fetching games:', error);
        }
    };

    useEffect(() => {
        console.log("connect to lobby");
        fetchData();

        if (services.webSocketService.isWebSocketOpen()) {
            console.log("websocket is open");
            // Subscribe to /lobby channel
            services.webSocketService.subscribe('/topic/lobby', (message) => {
                console.log('Received message from /topic/lobby:', message);
                setPokerGames((prevGameList) => {
                    return services.lobbyService.updateGameList(message, prevGameList);
                });
            });
        }

        // Cleanup WebSocket subscription on component unmount
        return () => {
            services.webSocketService.unsubscribe('/topic/lobby');
        };
    }, []);
    return (
        <>
            <Container mt="md" size="xl">
                <Flex
                    mih={90}
                    gap="md"
                    justify="flex-end"
                    align="center"
                    direction="row"
                    wrap="wrap"
                >
                    <Button leftSection={<IconPlus size={20} />} variant="filled" color="cyan" size="md"
                            onClick={open}>CRATE GAME</Button>
                </Flex>

                <GameList games={pokerGames}/>
            </Container>
            <CreateGameModal opened={opened} close={close}/>
        </>
    )
}