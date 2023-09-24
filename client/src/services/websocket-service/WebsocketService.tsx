import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {useKeycloak} from "@react-keycloak/web";

class WebSocketService {

    private stompClient: Stomp.Client | null = null;

    private subscriptions: { [channel: string]: Stomp.Subscription } = {};
    keyCloak = useKeycloak()
    token: string;

    private eventHandlers: { [event: string]: (() => void)[] } = {
        connected: [],
        disconnected: [],
    };
    constructor() {
        this.stompClient = null;
        this.token = this.keyCloak.keycloak.token || '';
    }

    connect() {
        const socket = new SockJS('http://localhost:9080/ws'); //?access_token=' + this.token
        this.stompClient = Stomp.over(socket);
        const headers = {
            'Authorization': 'Bearer ' + this.token,
        }
        this.stompClient.connect(headers, () => {
            this.emit('connected');
            console.log('Connected to WebSocket');
        });
    }


    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect( () => {
                this.emit('disconnected');
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
        });
        console.log(this.subscriptions)
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


    on(event: string, handler: () => void) {
        this.eventHandlers[event].push(handler);
    }

    off(event: string, handler: () => void) {
        this.eventHandlers[event] = this.eventHandlers[event].filter((h) => h !== handler);
    }

    emit(event: string) {
        for (const handler of this.eventHandlers[event]) {
            handler();
        }
    }
}

export default WebSocketService;