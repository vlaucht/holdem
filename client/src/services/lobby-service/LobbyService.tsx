import {ApiController} from "../api-controller/ApiController";
import {PokerGameLobbyDto} from "../../models/PokerGameLobbyDto";

class LobbyService {
    private apiController: ApiController;

    // TODO get from env
    private readonly LOBBY_CONTROLLER_URL = 'api/lobby'

    constructor() {
        // TODO: get base url from env
        this.apiController = new ApiController('http://localhost:9080');
    }

    async getAll(): Promise<PokerGameLobbyDto[]> {
        return await this.apiController.getRequest<PokerGameLobbyDto[]>(`${this.LOBBY_CONTROLLER_URL}`);
    }


    updateGameList(game: PokerGameLobbyDto, gameList: PokerGameLobbyDto[]): PokerGameLobbyDto[] {
        if (game.operation === 'CREATE') {
            console.log('Adding game to list:', game);
            console.log([...gameList, game]);
            return [...gameList, game];
        } else if (game.operation === 'UPDATE') {
            return gameList.map((oldGame) => {
                return oldGame.gameId === game.gameId ? game : oldGame;
            });
        } else if (game.operation === 'DELETE') {
            return gameList.filter((oldGame) => oldGame.gameId !== game.gameId);
        }
        return gameList;
    }

}

export default LobbyService;