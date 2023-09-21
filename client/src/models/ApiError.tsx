/**
 * Interface that formats an api error to access the error elements
 *
 * @author Valentin Laucht
 * @version 1.0
 */
export interface ApiError {
    error: string;
    message: string;
    status: number;
}