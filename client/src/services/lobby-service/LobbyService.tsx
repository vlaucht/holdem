import {ApiController} from "../api-controller/ApiController";
import {PokerGameLobbyDto} from "../../models/PokerGameLobbyDto";
import {PokerGameState} from "../../models/PokerGameState";
import {PokerGameCreateRequest} from "../../models/PokerGameCreateRequest";

class LobbyService {
    private apiController: ApiController;

    // TODO get from env
    private readonly LOBBY_CONTROLLER_URL = 'api/lobby'
    private readonly POKER_CONTROLLER_URL = 'api/poker'

    constructor() {
        // TODO: get base url from env
        this.apiController = new ApiController('http://localhost:9080');
    }

    async getAll(): Promise<PokerGameLobbyDto[]> {
        return await this.apiController.getRequest<PokerGameLobbyDto[]>(`${this.LOBBY_CONTROLLER_URL}`);
    }

    async create(payload: PokerGameCreateRequest): Promise<PokerGameState> {
        return await this.apiController.postRequest<PokerGameState>(`${this.POKER_CONTROLLER_URL}/create`, payload);
    }

    updateGameList(game: PokerGameLobbyDto, gameList: PokerGameLobbyDto[]): PokerGameLobbyDto[] {
        if (game.operation === 'CREATE') {
            return [...gameList, game];
        } else if (game.operation === 'UPDATE') {
            return gameList.map((oldGame) => {
                return oldGame.gameId === game.gameId ? game : oldGame;
            });
        } else if (game.operation === 'DELETE') {
            return gameList.filter((game) => game.gameId !== game.gameId);
        }
        return gameList;
    }

}

export default LobbyService;