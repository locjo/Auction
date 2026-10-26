import { apiClient } from "../lib/apiClient";
import type { ApiResponse } from "../types/api";
import type { BidResponse, PlaceBidRequest } from "../types/auction";

export const bidService = {
  async placeBid(
    productId: number,
    request: PlaceBidRequest,
  ): Promise<BidResponse> {
    const { data } = await apiClient.post<ApiResponse<BidResponse>>(
      `/api/v1/products/${productId}/bids`,
      request,
    );
    return data.data;
  },
};
