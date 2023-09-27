import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {useKeycloak} from "@react-keycloak/web";
import {ApiError} from "../../models/ApiError";
import {displayError, displayLocalError} from "../ToastService";

class WebSocketService {

    private stompClient: Stomp.Client | null = null;

    private subscriptions: { [channel: string]: Stomp.Subscription } = {};
    private errorChannelSubscription: Stomp.Subscription | null = null;
    private keyCloak = useKeycloak()
    private readonly token: string;
    private isConnected: boolean = false;

    private eventHandlers: { [event: string]: (() => void)[] } = {
        connected: [],
        disconnected: [],
    };
    constructor() {
        this.stompClient = null;
        this.token = this.keyCloak.keycloak.token || '';
    }

    getSubscriptions() {
        return this.subscriptions;
    }

    isWebSocketOpen(): boolean {
        return this.isConnected;
    }

    connect() {
        const socket = new SockJS('http://localhost:9080/ws?access_token=' + this.token);
        this.stompClient = Stomp.over(socket);
        const headers = {
            'Authorization': 'Bearer ' + this.token,
        }
        this.stompClient.connect(headers, () => {
            this.subscribeToErrorChannel();
            this.emit('connected');
            console.log('Connected to WebSocket');
            this.isConnected = true;
        });
    }


    disconnect() {
        if (this.stompClient) {
            this.unsubscribeFromErrorChannel();
            this.stompClient.disconnect( () => {
                this.emit('disconnected');
                console.log('Disconnected from WebSocket');
                this.isConnected = false;
            });
        }
    }

    subscribe(channel: string, callback: (arg0: any) => void) {
        if (!this.stompClient) {
            displayLocalError('Error connecting to WebSocket');
            return;
        }
        this.subscriptions[channel] = this.stompClient.subscribe(channel, (message) => {
            callback(JSON.parse(message.body));
        });
    }

    unsubscribe(channel: string) {
        if (this.subscriptions[channel]) {
            this.subscriptions[channel].unsubscribe();
            delete this.subscriptions[channel];
        }
    }

    subscribeToErrorChannel() {
        if (!this.stompClient) {
            displayLocalError('Error connecting to WebSocket');
            return;
        }

        const errorChannel = '/user/queue/errors';
        this.errorChannelSubscription = this.stompClient.subscribe(errorChannel, (message) => {
            displayError(JSON.parse(message.body));
        });
    }

    unsubscribeFromErrorChannel() {
        if (this.errorChannelSubscription) {
            this.errorChannelSubscription.unsubscribe();
            this.errorChannelSubscription = null;
        }
    }


    sendMessage(channel: string, message: any) {
        if (!this.stompClient) {
            displayLocalError('Error connecting to WebSocket');
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