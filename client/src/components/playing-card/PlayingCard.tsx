import React from "react";
import './PlayingCard.css';

export interface PlayingCardProps {
    suit?: string;
    rank?: string;
    color?: 'red' | 'black';
    isFaceUp?: boolean;
    className?: string;
}

export const PlayingCard: React.FunctionComponent<PlayingCardProps> = ({suit, rank, color, isFaceUp, className}) => {
    const cardClass = isFaceUp ? `card ${color}` : 'card card-back';
    return (
        <div className={`${cardClass} ${className || ''}`}>
            {isFaceUp ? (
                <>
                    <div className="card-top">
                        <span className="rank">{rank}</span>
                        <span className="suit">{suit}</span>
                    </div>
                    <div className="card-center">
                        <span className="suit">{suit}</span>
                    </div>
                    <div className="card-bottom">
                        <span className="rank">{rank}</span>
                        <span className="suit">{suit}</span>
                    </div>
                </>
            ) : (
                <div className="card-back-content">

                </div>
            )}
        </div>

    )
}