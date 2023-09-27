import { notifications } from '@mantine/notifications';
import {ErrorFormat} from "./api-controller/ApiController";

export const displayError = (error: { message: any; }) => {
    const errorFormat = formatError(error);
    notifications.show({
        title: 'Error',
        message: errorFormat.message,
        color: 'red',
    });
}

export const displayLocalError = (error: string) => {
    notifications.show({
        title: 'Error',
        message: error,
        color: 'red',
    });
}

export const displayErrorFormat = (error: ErrorFormat) => {
    notifications.show({
        title: error.title,
        message: error.message,
        color: 'red',
    });
}

/**
 * Method to format an API error with meaningful messages.
 *
 * @param error the error to format
 */
const formatError = (error: { message: any; }): ErrorFormat => {
    const title: string = 'Something went wrong.';
    let message;
    message = error.message;
    return {title, message, error: 'error'};
}