import Axios, { AxiosError } from "axios";
import {ApiError} from "../../models/ApiError";

/**
 * Interface to format an error from an API Request
 *
 * @author Valentin Laucht
 * @version 1.0
 */
export interface ErrorFormat {
    title: string;
    message: string;
    error: string;
}

/**
 * Controller to handle requests to the api
 *
 * @author Valentin Laucht
 * @version 1.0
 */
export class ApiController {
    private apiUrl: string;

    constructor(apiUrl: string) {
        this.apiUrl = apiUrl;
    }

    /**
     * Generic GET request. Sends a GET request to the api and formats the result
     * with the provided type.
     *
     * @typedef T the type of the expected return data
     * @param URL the url for the request excluding the base url
     * @param params optional request params
     */
    getRequest<T>(URL: string, params?: {}): Promise<T> {
        return new Promise<any>((resolve, reject) => {
            Axios.get(`${this.apiUrl}/${URL}`, {params: params})
                .then((res) => {
                    resolve(JSON.parse(JSON.stringify(res.data)) as T)
                }).catch((err: AxiosError) => {
                reject(this.formatError(err))
            });
        });
    }

    /**
     * Generic DELETE request. Sends a DELETE request to the api.
     *
     * @param URL the url for the request excluding the base url
     * @param params optional request params
     */
    deleteRequest(URL: string, params?: {}): Promise<void> {
        return new Promise<void>((resolve, reject) => {
            Axios.delete(`${this.apiUrl}/${URL}`, {params: params})
                .then(() => resolve())
                .catch((err: AxiosError) => {
                    reject(this.formatError(err));
                });
        });
    }

    /**
     * Generic POST request. Sends a POST request to the api.
     *
     * @param URL the url for the request excluding the base url
     * @param payload optional request payload
     * @param params optional request params
     */
    async postRequest<T>(URL: string, payload?: {}, params?: {}): Promise<T> {
        return new Promise<any>((resolve, reject) => {
            Axios.post(`${this.apiUrl}/${URL}`, payload, {params: params})
                .then((res) => {
                    resolve(JSON.parse(JSON.stringify(res.data)) as T)
                }).catch((err: AxiosError) => {
                reject(this.formatError(err))
            });
        });
    }

    /**
     * Method to format an API error with meaningful messages.
     *
     * @param error the error to format
     */
    formatError(error: AxiosError): ErrorFormat {
        const title: string = 'Etwas ist schiefgelaufen.';
        let message;
        if (error.code === "ERR_NETWORK") {
            message = "Service ist nicht erreichbar."
            return {title, message, error: message};
        }
        const apiError: ApiError = error.response?.data as ApiError;
        message = apiError.message ? apiError.message : apiError.error;
        return {title, message, error: apiError.error};
    }
}