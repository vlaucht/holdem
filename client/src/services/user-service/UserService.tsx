import {ApiController} from "../api-controller/ApiController";
import {UserExtra} from "../../models/UserExtra";

/**
 * Service to handle all logic related to {@link UserExtra}
 *
 * @author Valentin Laucht
 * @version 1.0
 */
export class UserService {
    private apiController: ApiController;

    private readonly USER_CONTROLLER_URL = 'api/user'

    constructor() {
        // TODO: get base url from env
        this.apiController = new ApiController('http://localhost:9080');
    }

    /**
     * Method to get {@link UserExtra} from the api
     *
     * @return a promise with the user data or an error
     */
    async getUserExtra(): Promise<UserExtra> {
        return await this.apiController.getRequest<UserExtra>(`${this.USER_CONTROLLER_URL}/me`);
    }

}