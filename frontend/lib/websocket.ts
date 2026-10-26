import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const socketUrl = import.meta.env.VITE_WS_URL ?? "/ws";

export function createStompClient(accessToken?: string | null): Client {
  return new Client({
    webSocketFactory: () => new SockJS(socketUrl) as WebSocket,
    connectHeaders: accessToken
      ? { Authorization: `Bearer ${accessToken}` }
      : {},
    reconnectDelay: 4_000,
  });
}
