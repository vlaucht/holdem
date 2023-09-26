import React, { useEffect, useRef } from "react";
import { PokerGameState } from "../../models/PokerGameState";
import { PlayingCard } from "../playing-card/PlayingCard";
import { Group } from "@mantine/core";
import { Seat } from "../seat/Seat";
import { useUser } from "../../hooks/user-provider/UserProvider";

import "./PokerTable.css";
import {animateBoardCardFly} from "./DealCard";

interface GameTableProps {
    game: PokerGameState;
}

export const PokerTable: React.FunctionComponent<GameTableProps> = ({ game }) => {

    /** Used so that it doesn't crash when players are waiting and no cards are dealt yet */
    const showBoardCards = game.flopCards && game.flopCards.length > 0 && game.turnCard && game.riverCard;

    /** The current user */
    const user = useUser().user;

    /** The index of the current user in the player list. */
    const userIndex = game.players.findIndex((player) => player.name === user.username);

    /** The index of the dealer in the player list. Used to determine the origin of the dealing card animation. */
    const dealerIndex = game.players.findIndex((player) => player.dealer);

    /** Hold references of each seat, so it can be mapped to a player */
    const seatElements: (HTMLElement | null)[] = [];

    const boardCardRefs: (React.RefObject<HTMLElement> | null)[] = [
        useRef(null), // For flop card 1
        useRef(null), // For flop card 2
        useRef(null), // For flop card 3
        useRef(null), // For turn card
        useRef(null), // For river card
    ];

    /** Reference to the animated card */
    const flyingCardRef = useRef<HTMLDivElement | null>(null);

    /**
     * Create a seat for each player in the game.
     *
     * <p>
     *     The seats are ordered based on the user's position in the game.
     *     The user's seat is always the first seat.
     *     Creates also a map that references each player to their seat and will be used to animate the cards when dealing.
     * </p>
     */
    const seats = game.players.map((player, index) => {
        const totalPlayers = game.players.length;
        const relativeIndex = (index - userIndex + totalPlayers) % totalPlayers;
        const seatNumber = relativeIndex === 0 ? 1 : relativeIndex + 1;

        const setSeatRef = (el: HTMLElement | null) => {
            seatElements[index] = el;
        };

        return (
            <div
                className={`seat-${seatNumber}`}
                key={`seat-${player.name}`}
                id={player.dealer ? "dealer-seat" : undefined}
                ref={setSeatRef}
            >
                <Seat player={player} cards={player.cards} />
            </div>
        );
    });

    useEffect(() => {
        console.log(boardCardRefs)
        console.log(seatElements)
        // toggleHoleCardVisibility();
        animateBoardCardFly(seatElements[dealerIndex]!, boardCardRefs, flyingCardRef);
        // animateSeatCardFly(game, seatElements, flyingCardRef, dealerIndex, 2);
    }, []);


    return (
        <div className="poker-table-container">
            <div className="poker-table">
                <div className="board">
                    <Group>
                        {showBoardCards && (
                            <>
                                {game.flopCards.map((card, index) => (
                                    <PlayingCard
                                        key={index}
                                        card={card}
                                        ref={boardCardRefs[index]} // Use the appropriate ref for each flop card
                                    />
                                ))}
                                <PlayingCard key={"turnCard"} card={game.turnCard} ref={boardCardRefs[3]} />
                                <PlayingCard key={"riverCard"} card={game.riverCard} ref={boardCardRefs[4]} />
                            </>
                        )}
                    </Group>
                </div>
                {seats}
            </div>
            <div className="flying-card" ref={flyingCardRef}>
            </div>
        </div>
    );
};
