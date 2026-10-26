import { type IMessage, type StompSubscription } from "@stomp/stompjs";
import { createStompClient } from "../lib/websocket";
import type {
  AuctionSocketEvent,
  AuctionUpdatePayload,
} from "../types/auction";

export function connectToAuction(
  productId: number,
  onUpdate: (update: AuctionUpdatePayload) => void,
  onError: (message: string) => void,
  accessToken?: string | null,
): () => void {
  let subscription: StompSubscription | undefined;
  const client = createStompClient(accessToken);
  client.onConnect = () => {
    subscription = client.subscribe(
      `/topic/products/${productId}/bids`,
      (message: IMessage) => handleMessage(message, onUpdate, onError),
    );
  };
  client.onStompError = (frame) =>
    onError(frame.headers.message ?? "Mất kết nối phiên đấu giá.");

  client.activate();
  return () => {
    subscription?.unsubscribe();
    void client.deactivate();
  };
}

function handleMessage(
  message: IMessage,
  onUpdate: (update: AuctionUpdatePayload) => void,
  onError: (message: string) => void,
): void {
  try {
    const event = JSON.parse(message.body) as AuctionSocketEvent;
    if (event.type === "BID_PLACED") onUpdate(event.payload);
    if (event.type === "ERROR") onError(event.payload.message);
  } catch {
    onError("Dữ liệu cập nhật phiên đấu giá không hợp lệ.");
  }
}
