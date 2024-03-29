import {ApiController} from "../api-controller/ApiController";
import {UserExtra} from "../../models/UserExtra";
import {PokerGameState} from "../../models/PokerGameState";
import {PokerGameCreateRequest} from "../../models/PokerGameCreateRequest";

/**
 * Service to handle all logic related to {@link UserExtra}
 *
 * @author Valentin Laucht
 * @version 1.0
 */
export class PokerService {
    private apiController: ApiController;

    private readonly POKER_CONTROLLER_URL = 'api/poker'

    constructor() {
        // TODO: get base url from env
        this.apiController = new ApiController('http://localhost:9080');
    }

    /**
     * Method to get {@link PokerGameState} from the api
     *
     * @return a promise with game state or an error
     */
    async getPokerGameState(gameId: string): Promise<PokerGameState> {
        return await this.apiController.getRequest<PokerGameState>(`${this.POKER_CONTROLLER_URL}/state/${gameId}`);
    }

    async create(payload: PokerGameCreateRequest): Promise<PokerGameState> {
        return await this.apiController.postRequest<PokerGameState>(`${this.POKER_CONTROLLER_URL}/create`, payload);
    }

    async leave(gameId: string): Promise<void> {
        return await this.apiController.postRequest<void>(`${this.POKER_CONTROLLER_URL}/leave/${gameId}`);
    }

    async join(gameId: string): Promise<PokerGameState> {
        return await this.apiController.postRequest<PokerGameState>(`${this.POKER_CONTROLLER_URL}/join/${gameId}`);
    }

    async startGame(gameId: string): Promise<PokerGameState> {
        return await this.apiController.postRequest<PokerGameState>(`${this.POKER_CONTROLLER_URL}/start/${gameId}`);
    }

}