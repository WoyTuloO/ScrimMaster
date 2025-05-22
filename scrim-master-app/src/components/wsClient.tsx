import { Client } from '@stomp/stompjs'
import SockJS      from 'sockjs-client'

export const connectStomp = (onConnect: () => void): Client => {
    const client = new Client({
        webSocketFactory: () => new SockJS('/ws'),
        reconnectDelay: 5000,
        heartbeatIncoming: 20000,
        heartbeatOutgoing: 20000,
        onConnect,
    })

    client.onStompError = frame => {
        console.error('Broker error', frame.headers['message'], frame.body)
    }

    client.activate()
    return client
}
