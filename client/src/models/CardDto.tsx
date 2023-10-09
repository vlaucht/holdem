export interface CardDto {
    suit: string;
    rank: string;
    color: 'red' | 'black';
    isFaceUp: boolean;
}

export function compareCards(card1: CardDto, card2: CardDto): number {
    // Compare suit, rank, and color
    if (
        card1.suit === card2.suit &&
        card1.rank === card2.rank &&
        card1.color === card2.color
    ) {
        return 1; // Cards are equal
    }

    // Cards are not equal
    return -1;
}