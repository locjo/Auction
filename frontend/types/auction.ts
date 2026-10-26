export type ProductStatus =
  "PENDING" | "ACTIVE" | "SCHEDULED" | "ENDED" | "CANCELLED";

export interface ProductDto {
  id: number;
  title: string;
  description: string | null;
  categoryId: number;
  categoryName: string;
  sellerId: number;
  sellerName: string;
  initialPrice: number | string;
  currentPrice: number | string;
  startTime: string | null;
  endTime: string | null;
  status: ProductStatus;
  bidCount: number;
  createdAt: string;
}

export interface AuctionProduct {
  id: number;
  title: string;
  category: string;
  price: number;
  initialPrice?: number;
  bids: number;
  time: string;
  image: string;
  sellerName?: string;
  description?: string | null;
  endTime?: string | null;
  status?: ProductStatus;
}

export interface PlaceBidRequest {
  bidAmount: number;
}

export interface BidResponse {
  id: number;
  productId: number;
  userId: number;
  bidderName: string;
  bidAmount: number | string;
  createdAt: string;
}

export interface AuctionUpdatePayload {
  productId: number;
  currentPrice: number | string;
  bidderName: string;
  bidAmount: number | string;
  endTime?: string;
  createdAt: string;
}

export type AuctionSocketEvent =
  | { type: "BID_PLACED"; payload: AuctionUpdatePayload }
  | { type: "AUCTION_ENDED"; payload: { productId: number; endTime: string } }
  | { type: "ERROR"; payload: { code: string; message: string } };

