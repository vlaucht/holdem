import {CardDto} from "./CardDto";

export type PokerPlayerAction =
    | 'fold'
    | 'check'
    | 'call'
    | 'raise'
    | 'allIn'
    | 'smallBlind'
    | 'bigBlind'
    | 'none';
export interface PokerPlayerDto {
    name: string;
    avatar: string;
    cards: CardDto[];
    chips: number;
    bet: number;
    potShare: number;
    lastAction: PokerPlayerAction;
    dealer: boolean;
    bigBlind: boolean;
    smallBlind: boolean;
    isActor: boolean;
    allowedActions: PokerPlayerAction[];
    mustShowCards: boolean;
}