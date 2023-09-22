export interface PokerGameCreateRequest {
    name: string;
    buyIn: number;
    maxPlayerCount: number;
    tableType: 'NL' | 'FL';
}