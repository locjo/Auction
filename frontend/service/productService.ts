import { isAxiosError } from "axios";
import { apiClient } from "../lib/apiClient";
import type { ApiResponse } from "../types/api";
import type { AuctionProduct, ProductDto } from "../types/auction";

const fallbackImages = [
  "https://images.unsplash.com/photo-1523170335258-f5ed11844a49?auto=format&fit=crop&w=900&q=85",
  "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=900&q=85",
  "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?auto=format&fit=crop&w=900&q=85",
  "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=900&q=85",
];

export function mapProductDto(product: ProductDto): AuctionProduct {
  const category = product.categoryName || "Sưu tầm";
  const imageIndex = category.includes("Ô tô")
    ? 1
    : category.includes("Nghệ")
      ? 2
      : 0;
  const isExpired = product.endTime
    ? Date.parse(product.endTime) <= Date.now()
    : false;
  return {
    id: product.id,
    title: product.title,
    category,
    price: Number(product.currentPrice),
    initialPrice: Number(product.initialPrice),
    bids: product.bidCount ?? 0,
    time: product.endTime
      ? new Intl.DateTimeFormat("vi-VN", {
          dateStyle: "short",
          timeStyle: "short",
        }).format(new Date(product.endTime))
      : "Chưa lên lịch",
    image: fallbackImages[imageIndex] ?? fallbackImages[3]!,
    sellerName: product.sellerName,
    description: product.description,
    endTime: product.endTime,
    status: product.status === "ACTIVE" && isExpired ? "ENDED" : product.status,
  };
}

export const productService = {
  async listPublic(keyword?: string): Promise<AuctionProduct[]> {
    const { data } = await apiClient.get<ApiResponse<ProductDto[]>>(
      "/api/v1/products",
      { params: { status: "ALL", keyword } },
    ).catch(async (error: unknown) => {
      // Older backend builds reject status=ALL; retry without the new filter.
      if (!isAxiosError(error) || error.response?.status !== 400) throw error;
      return apiClient.get<ApiResponse<ProductDto[]>>("/api/v1/products", {
        params: { keyword },
      });
    });
    return data.data.map(mapProductDto);
  },

  async getById(id: number): Promise<ProductDto> {
    const { data } = await apiClient.get<ApiResponse<ProductDto>>(
      `/api/v1/products/${id}`,
    );
    return data.data;
  },
};
