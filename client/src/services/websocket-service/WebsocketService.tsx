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
        const socket = new SockJS('http://localhost:9080/ws');
        this.stompClient = Stomp.over(socket);
        const headers = {
            'Authorization': `Bearer ${this.token}`
        };
        this.stompClient.connect(headers, () => {
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
        console.log(this.stompClient);
        console.log("trying to subscribe to channel: " + channel);
        if (!this.stompClient) {
            // TODO: throw error
            return;
        }
        console.log("subscribing to channel: " + channel);
        this.stompClient.subscribe(channel, (message) => {

                callback(JSON.parse(message.body));
        },     {
            'Authorization': `Bearer ${this.token}`
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