import React from "react";
import {PokerGameState} from "../../models/PokerGameState";
import './PokerTable.css';
import {PlayingCard} from "../playing-card/PlayingCard";
import {Group} from "@mantine/core";
import {Seat} from "../seat/Seat";

interface GameTableProps {
    game: PokerGameState;
}

export const PokerTable: React.FunctionComponent<GameTableProps> = ({game}) => {

    return (
        <div className="poker-table-container">
            <div className="poker-table">
                <div className="board">
                    <Group>
                        <PlayingCard  isFaceUp={false}></PlayingCard>
                        <PlayingCard  isFaceUp={true} color="red" rank="K" suit="&clubs;"></PlayingCard>
                    </Group>

                </div>
                <div className="seat-1">
                    <Seat name={"Player"} bet={100} chips={1000} cards={[{isFaceUp: false}, {isFaceUp: false}]}/>
                </div>
                <div className="seat-2">
                    <Seat name={"Player"} bet={100} chips={1000} cards={[{isFaceUp: false}, {isFaceUp: false}]}/>
                </div>
                <div className="seat-3">
                    <Seat name={"Player"} bet={100} chips={1000} cards={[{isFaceUp: false}, {isFaceUp: false}]}/>
                </div>
                <div className="seat-4">
                    <Seat name={"Player"} bet={100} chips={1000} cards={[{isFaceUp: false}, {isFaceUp: false}]}/>
                </div>
                <div className="seat-5">
                    <Seat name={"Player"} bet={100} chips={1000} cards={[{isFaceUp: false}, {isFaceUp: false}]}/>
                </div>

            </div>
        </div>

    )
}