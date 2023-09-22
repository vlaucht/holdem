import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {useKeycloak} from "@react-keycloak/web";

class WebSocketService {

    private stompClient: Stomp.Client | null = null;

    private subscriptions: { [channel: string]: Stomp.Subscription } = {};
    keyCloak = useKeycloak()
    token: string;
    constructor() {
        this.stompClient = null;
        this.token = this.keyCloak.keycloak.token || '';
    }

    connect() {
        const socket = new SockJS('http://localhost:9080/ws?access_token=' + this.token);
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, () => {
            console.log('Connected to WebSocket');
        });
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect( () => {
                console.log('Disconnected from WebSocket');
            });
        }
    }

    subscribe(channel: string, callback: (arg0: any) => void) {
        if (!this.stompClient) {
            // TODO: throw error
            return;
        }
        this.stompClient.subscribe(channel, (message) => {
                callback(JSON.parse(message.body));
        });
    }

    unsubscribe(channel: string) {
        if (this.subscriptions[channel]) {
            this.subscriptions[channel].unsubscribe();
            delete this.subscriptions[channel];
        }
    }

    sendMessage(channel: string, message: any) {
        if (!this.stompClient) {
            // TODO: throw error
            return;
        }
        this.stompClient.send(channel, {}, JSON.stringify(message));

    }
}

export default WebSocketService;