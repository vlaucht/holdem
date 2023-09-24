import React, {createContext, ReactNode, useContext} from "react";
import WebSocketService from "../../services/websocket-service/WebsocketService";
import LobbyService from "../../services/lobby-service/LobbyService";
import {UserService} from "../../services/user-service/UserService";

interface ServiceContextType {
    webSocketService: WebSocketService;
    lobbyService: LobbyService;
    userService: UserService;
}
const ServiceContext = createContext<ServiceContextType | undefined>(undefined);

export const ServiceProvider: React.FC<{children: ReactNode}> = ({  children }) => {
    const webSocketService = new WebSocketService();
    const lobbyService = new LobbyService();
    const userService = new UserService();

    return (
          <ServiceContext.Provider value={{webSocketService, lobbyService, userService}}>
                {children}
            </ServiceContext.Provider>
    );
}

export function useServices() {
    const context = useContext(ServiceContext);
    if (context === undefined) {
        throw new Error('useService must be used within a ServiceProvider');
    }
    return context;
}