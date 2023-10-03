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
    holeCards: CardDto[];
    handResult: {handType: string, handCards: CardDto[]};
    chips: number;
    bet: number;
    potShare: number;
    lastAction: PokerPlayerAction;
    isDealer: boolean;
    isBigBlind: boolean;
    isSmallBlind: boolean;
    isActor: boolean;
    allowedActions: PokerPlayerAction[];
    mustShowCards: boolean;
}