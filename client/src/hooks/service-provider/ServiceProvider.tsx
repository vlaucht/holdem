import React, {createContext, ReactNode, useContext} from "react";
import WebSocketService from "../../services/websocket-service/WebsocketService";
import LobbyService from "../../services/lobby-service/LobbyService";

interface ServiceContextType {
    webSocketService: WebSocketService;
    lobbyService: LobbyService;
}
const ServiceContext = createContext<ServiceContextType | undefined>(undefined);

export const ServiceProvider: React.FC<{children: ReactNode}> = ({  children }) => {
    const webSocketService = new WebSocketService();
    const lobbyService = new LobbyService();

    return (
          <ServiceContext.Provider value={{webSocketService, lobbyService}}>
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