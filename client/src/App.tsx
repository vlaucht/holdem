import React from 'react';
import './App.css';
import '@mantine/core/styles.css';
import {MantineProvider} from "@mantine/core";
import {ReactKeycloakProvider} from "@react-keycloak/web";
import keycloak from "./keycloak/keycloak";
import {ContentLoader} from "./components/loader/ContentLoader";
import {RequestInterceptor} from "./services/interceptor/RequestInterceptor";
import AuthenticatedTemplate from "./pages/authenticated-template/AuthenticatedTemplate";
import {ServiceProvider} from "./hooks/service-provider/ServiceProvider";


const App = () => {
    const keycloakProviderConfig = {
        initOptions: {
            onLoad: 'login-required',
            pkceMethod: 'S256'
        },
        authClient: keycloak,
    };
    return (
        <MantineProvider defaultColorScheme="dark">
        <ReactKeycloakProvider {...keycloakProviderConfig} LoadingComponent={<ContentLoader text="Logging in..."/>}>
            <RequestInterceptor>
                <ServiceProvider>
                    <AuthenticatedTemplate/>
                </ServiceProvider>
            </RequestInterceptor>
        </ReactKeycloakProvider>
        </MantineProvider>
    )
}

export default App
