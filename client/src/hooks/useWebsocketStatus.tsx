import { useState, useEffect } from 'react';
import WebSocketService from "../services/websocket-service/WebsocketService";

function useWebSocketStatus(webSocketService: WebSocketService) {
    const [isConnected, setIsConnected] = useState(false);

    useEffect(() => {
        const onConnected = () => {
            setIsConnected(true);
        };

        const onDisconnected = () => {
            setIsConnected(false);
        };

        // Subscribe to WebSocket connection state changes
        webSocketService.on('connected', onConnected);
        webSocketService.on('disconnected', onDisconnected);

        // Clean up the subscriptions when the component unmounts
        return () => {
            webSocketService.off('connected', onConnected);
            webSocketService.off('disconnected', onDisconnected);
        };
    }, [webSocketService]);

    return { isConnected };
}

export default useWebSocketStatus;