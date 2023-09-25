import React, {createContext, ReactNode, useContext} from "react";
import WebSocketService from "../../services/websocket-service/WebsocketService";
import LobbyService from "../../services/lobby-service/LobbyService";
import {UserService} from "../../services/user-service/UserService";
import {PokerService} from "../../services/poker-service/PokerService";

interface ServiceContextType {
    webSocketService: WebSocketService;
    lobbyService: LobbyService;
    userService: UserService;
    pokerService: PokerService;
}
const ServiceContext = createContext<ServiceContextType | undefined>(undefined);

export const ServiceProvider: React.FC<{children: ReactNode}> = ({  children }) => {
    const webSocketService = new WebSocketService();
    const lobbyService = new LobbyService();
    const userService = new UserService();
    const pokerService = new PokerService();

    return (
          <ServiceContext.Provider value={{webSocketService, lobbyService, userService, pokerService}}>
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