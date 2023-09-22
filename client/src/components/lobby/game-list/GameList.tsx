import React from "react";
import {Table} from "@mantine/core";
import {PokerGameLobbyDto} from "../../../models/PokerGameLobbyDto";

interface GameListProps {
    games: PokerGameLobbyDto[];
}

export const GameList: React.FunctionComponent<GameListProps> = ({games}) => {

    return (
            <Table highlightOnHover horizontalSpacing="xl">
                <Table.Thead>
                    <Table.Tr>
                    <Table.Th>NAME</Table.Th>
                    <Table.Th>PLAYERS</Table.Th>
                    <Table.Th>BUI-IN</Table.Th>
                    <Table.Th>STATUS</Table.Th>
                    <Table.Th>TABLE</Table.Th>
                    <Table.Th>ACTIONS</Table.Th>
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
                            <Table.Td>TODO</Table.Td>
                        </Table.Tr>
                    ))}
                </Table.Tbody>
            </Table>
    )
}