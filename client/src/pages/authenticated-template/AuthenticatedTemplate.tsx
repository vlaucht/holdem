import {useKeycloak} from "@react-keycloak/web";
import {Shell} from "../shell/Shell";
import React, {useEffect} from "react";
import {ErrorPage} from "../../components/error/ErrorPage";
import {UserProvider} from "../../hooks/user-provider/UserProvider";
import {useServices} from "../../hooks/service-provider/ServiceProvider";
import WebSocketService from "../../services/websocket-service/WebsocketService";
import {ContentLoader} from "../../components/loader/ContentLoader";
import useWebSocketStatus from "../../hooks/useWebsocketStatus";
import {BrowserRouter} from "react-router-dom";

const AuthenticatedTemplate = () => {
    const {keycloak} = useKeycloak();
    const webSocketService: WebSocketService = useServices().webSocketService;
    const {isConnected} = useWebSocketStatus(webSocketService);
    useEffect(() => {
        webSocketService.connect();

        // Cleanup WebSocket connection on component unmount
        return () => {
            webSocketService.disconnect();
        };
    }, []);

    return (
        keycloak.authenticated ?
            (
                isConnected ? (
                    <BrowserRouter>
                        <UserProvider>
                            <Shell/>
                        </UserProvider>
                    </BrowserRouter>
                ) : <ContentLoader text={"Connecting..."}/>
            )
            :
            (<ErrorPage text="You are not logged in."/>)
    )


}

export default AuthenticatedTemplate;