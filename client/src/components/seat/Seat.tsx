import React, { useEffect } from "react";
import './Seat.css';
import { PlayingCard } from "../playing-card/PlayingCard";
import { CardDto } from "../../models/CardDto";
import { PokerPlayerDto } from "../../models/PokerPlayerDto";

const actionText = {
    fold: "FOLDED",
    check: "CHECKED",
    call: "CALLED",
    raise: "RAISED",
    allIn: "ALL-IN",
    smallBlind: "SMALL BLIND",
    bigBlind: "BIG BLIND",
    none: ""
};

interface SeatProps {
    player: PokerPlayerDto;
    cards: CardDto[];
}

export const Seat: React.FunctionComponent<SeatProps> = ({ player, cards }) => {
    // Use useEffect to set the dealer chip's position based on dealerButtonRef
    const buttons = [];
    if (player.dealer) {
        buttons.push("D");
    }

    if (player.smallBlind) {
        buttons.push("SB");
    }

    if (player.bigBlind) {
        buttons.push("BB");
    }

    return (
        <div className="seat-container">
            <div className="seat">

                <div className="button-container">
                    {buttons.map((button, index) => (
                        <div key={index} className={`chip ${button.toLowerCase()}`}>
                            <div className="label">{button}</div>
                        </div>
                    ))}
                </div>

                <div key={"hole-cards"} className="hole-cards">
                    {cards &&  cards.map((card, index) => (
                        <PlayingCard key={index} card={card} className={`hole-card${index+1}`}/>
                    ))}
                </div>
                <div className="user-info">
                    <img src={player.avatar} className="avatar"></img>
                    <div className="info">
                        <div className="player-name">{player.name}</div>
                        <div className="chips">${player.chips}</div>
                    </div>
                </div>
                <div className="action">
                    {player.lastAction && `${actionText[player.lastAction]}`} (${player.bet})
                </div>

            </div>
        </div>
    );
}
