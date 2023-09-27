import React, {forwardRef} from "react";
import './PlayingCard.css';
import {CardDto} from "../../models/CardDto";

export interface PlayingCardProps {
    card: CardDto;
    className?: string;
}

export const PlayingCard = forwardRef<HTMLElement, PlayingCardProps>(
    ({ card, className }, ref) => {
    const cardClass = card.isFaceUp ? `card ${card.color}` : 'card card-back';
    const suitSymbol = { __html: card.suit };
    return (
        <div ref={ref as React.RefObject<HTMLDivElement>} className={`${cardClass} ${className || ''}`}>
            {card.isFaceUp ? (
                <>
                    <div className="card-top">
                        <span className="rank">{card.rank}</span>
                        <span className="suit" dangerouslySetInnerHTML={suitSymbol}></span>
                    </div>
                    <div className="card-center">
                        <span className="suit" dangerouslySetInnerHTML={suitSymbol}></span>
                    </div>
                    <div className="card-bottom">
                        <span className="rank">{card.rank}</span>
                        <span className="suit" dangerouslySetInnerHTML={suitSymbol}></span>
                    </div>
                </>
            ) : (
                <div className="card-back-content">

                </div>
            )}
        </div>

    )
}
);
