import { apiClient } from "../lib/apiClient";
import type { ApiResponse } from "../types/api";
import type { WalletDto } from "../types/wallet";

export const walletService = {
  async getMine(): Promise<WalletDto> {
    const { data } =
      await apiClient.get<ApiResponse<WalletDto>>("/api/v1/wallets/me");
    return data.data;
  },
};
