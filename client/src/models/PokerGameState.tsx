import {CardDto} from "./CardDto";
import {PokerPlayerDto} from "./PokerPlayerDto";

export interface PokerGameState {
    id: string;
    name: string;
    flopCards: CardDto[];
    turnCard: CardDto;
    riverCard: CardDto;
    operation: string;
    gameStatus: string;
    players: PokerPlayerDto[];
}