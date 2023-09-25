import React from "react";
import './Seat.css';
import {PlayingCard, PlayingCardProps} from "../playing-card/PlayingCard";

const actionText = {
    fold: "FOLDED",
    check: "CHECKED",
    call: "CALLED",
    raise: "RAISED",
    allIn: "ALL IN"
};

interface SeatProps {
    type?: 'dealer' | 'bigBlind' | 'smallBlind';
    name: string;
    cards: PlayingCardProps[];
    chips: number;
    action?: 'fold' | 'check' | 'call' | 'raise' | 'allIn';
    bet: number;
}

export const Seat: React.FunctionComponent<SeatProps> = ({type, name, cards, chips, action, bet}) => {
    const buttonName = type === 'dealer' ? 'D' : type === 'bigBlind' ? 'BB' : type === 'smallBlind' ? 'SB' : '';
    return (
        <div className="seat-container">
            <div className="seat">
                {type && (
                    <div className={`chip ${type}`}>
                        <div className="label">{buttonName}</div>
                    </div>
                )}
                <div className="hole-cards">
                    <PlayingCard className={"hole-card1"} isFaceUp={false}/>
                    <PlayingCard className={"hole-card2"} isFaceUp={false}/>
                </div>
                <div className="user-info">
                    <div className="avatar"></div>
                    <div className="info">
                        <div className="player-name">{name}</div>
                        <div className="chips">${chips}</div>
                    </div>
                </div>
                <div className="action">
                    {action && `${actionText[action]}`} (${bet})
                </div>

            </div>
        </div>
    );
}

