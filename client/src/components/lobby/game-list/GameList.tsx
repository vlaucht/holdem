import React from "react";
import {Button, Table} from "@mantine/core";
import {PokerGameLobbyDto} from "../../../models/PokerGameLobbyDto";
import {useServices} from "../../../hooks/service-provider/ServiceProvider";
import {useNavigate} from "react-router-dom";

interface GameListProps {
    games: PokerGameLobbyDto[];
}

export const GameList: React.FunctionComponent<GameListProps> = ({games}) => {
    const services = useServices();
    const navigate = useNavigate();
    const joinGame = async (gameId: string) => {
        const game = await services.pokerService.join(gameId);
        if (game.id) {
            navigate(`/poker-game/${game.id}`);
        }
    }
    return (
            <Table highlightOnHover horizontalSpacing="xl">
                <Table.Thead>
                    <Table.Tr>
                    <Table.Th>NAME</Table.Th>
                    <Table.Th>PLAYERS</Table.Th>
                    <Table.Th>BUI-IN</Table.Th>
                    <Table.Th>STATUS</Table.Th>
                    <Table.Th>TABLE</Table.Th>
                    <Table.Th></Table.Th>
                    </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                    {games.map((game) => (
                        <Table.Tr key={game.gameId}>
                            <Table.Td>{game.name}</Table.Td>
                            <Table.Td>{game.playerCount}/{game.maxPlayerCount}</Table.Td>
                            <Table.Td>{game.buyIn}</Table.Td>
                            <Table.Td>{game.gameStatus}</Table.Td>
                            <Table.Td>{game.tableType}</Table.Td>
                            <Table.Td><Button onClick={() => joinGame(game.gameId)}
                                              disabled={game.gameStatus !== 'Waiting for Players'}
                                              color="green">JOIN</Button></Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
    )
}