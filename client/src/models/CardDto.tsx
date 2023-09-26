export interface CardDto {
    suit: string;
    rank: string;
    color: 'red' | 'black';
    isFaceUp: boolean;
}