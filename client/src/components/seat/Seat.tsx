import React from "react";
import './Seat.css';
import { PlayingCard } from "../playing-card/PlayingCard";
import {CardDto, compareCards} from "../../models/CardDto";
import { PokerPlayerDto } from "../../models/PokerPlayerDto";
import {useUser} from "../../hooks/user-provider/UserProvider";
import {Actions} from "./Actions";
import {PokerGameState} from "../../models/PokerGameState";

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
    holeCards: CardDto[];
    game: PokerGameState;
}

export const Seat: React.FunctionComponent<SeatProps> = ({ player, holeCards, game }) => {
    const buttons = [];
    const user = useUser().user;
    if (player.isDealer) {
        buttons.push("D");
    }

    if (player.isSmallBlind) {
        buttons.push("SB");
    }

    if (player.isBigBlind) {
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
                    {holeCards &&  holeCards.map((card, index) => {

                        // Check if the card is in handCards and handResult is set
                        const isElevated = player.handResult && player.handResult.handCards.some(c => compareCards(c, card) === 1);
                        // Create a class string based on the condition
                        const cardClass = `hole-card${index + 1} ${isElevated ? 'elevated' : ''}`;

                        return(<PlayingCard key={index} card={card} className={cardClass} />)
                    })}
                </div>
                <div className={player.isActor ? "active-border glow" : "active-border"}>
                <div className="user-info">
                    <img alt={"Avatar"} src={player.avatar} className="avatar"></img>
                    <div className="info">
                        <div className="player-name">{player.name}</div>
                        <div className="chips">${player.chips}</div>
                    </div>
                </div>
                <div className="action">
                    {player.lastAction && `${actionText[player.lastAction]}`} (${player.bet})
                    {player.handResult && player.handResult.handType && (
                        <>
                            <br />
                            <div style={{ color: 'grey'}}>
                            {player.handResult.handType}
                            </div>

                        </>

                    )}
                </div>
                    {
                        (player.isActor && player.name == user.username) && (
                            <Actions player={player} game={game}/>)
                    }
                </div>
            </div>
        </div>
    );
}
