import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export function connectStomp(onConnect: () => void): Client {
    const client = new Client({
        webSocketFactory: () => {
            const sock = new SockJS("http://localhost:8080/ws");
            (sock as any).withCredentials = true;
            return sock;
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 20000,
        heartbeatOutgoing: 20000,
        debug: (msg) => console.debug("[STOMP]", msg),
    });

    client.onConnect = onConnect;
    client.onStompError = (frame) =>
        console.error("Broker error", frame.headers["message"], frame.body);

    client.activate();
    return client;
}
