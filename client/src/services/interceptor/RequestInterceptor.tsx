import React from 'react';
import axios from 'axios';
import { useKeycloak } from '@react-keycloak/web'; // Import useKeycloak from react-keycloak

interface RequestInterceptorProps {
    children: JSX.Element
}

/**
 * Adds the authentication header to every outgoing request.
 *
 * @author Valentin Laucht
 */
export const RequestInterceptor: React.FC<RequestInterceptorProps> = ({ children }) => {
    const { keycloak } = useKeycloak(); // Use the useKeycloak hook to get the Keycloak instance

    axios.interceptors.request.use(async (config) => {
        if (!keycloak.authenticated) {
            throw Error('User is not authenticated. Make sure the user is logged in.');
        }

        // Set the Authorization header with the access token from Keycloak
        config.headers.Authorization = `Bearer ${keycloak.token}`;

        return config;
    });

    return <>{children}</>;
};