import { create } from "zustand";
import type { AuctionUpdatePayload } from "../types/auction";

interface AuctionState {
  updatesByProduct: Record<number, AuctionUpdatePayload>;
  savedProductIds: number[];
  applyUpdate: (update: AuctionUpdatePayload) => void;
  toggleSaved: (productId: number) => void;
}

export const useAuctionStore = create<AuctionState>((set) => ({
  updatesByProduct: {},
  savedProductIds: [],
  applyUpdate: (update) =>
    set((state) => ({
      updatesByProduct: {
        ...state.updatesByProduct,
        [update.productId]: update,
      },
    })),
  toggleSaved: (productId) =>
    set((state) => ({
      savedProductIds: state.savedProductIds.includes(productId)
        ? state.savedProductIds.filter((id) => id !== productId)
        : [...state.savedProductIds, productId],
    })),
}));
