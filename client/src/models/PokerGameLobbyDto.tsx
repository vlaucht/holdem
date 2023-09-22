export interface PokerGameLobbyDto {
    gameId: string;
    name: string;
    playerCount: number;
    maxPlayerCount: number;
    tableType: string;
    buyIn: number;
    gameStatus: string;
    operation: 'CREATE' | 'UPDATE' | 'DELETE';
}