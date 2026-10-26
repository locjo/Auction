import { apiClient } from "../lib/apiClient";
import type { ApiResponse } from "../types/api";
import type { CategoryDto } from "../types/category";

export const categoryService = {
  async list(): Promise<CategoryDto[]> {
    const { data } = await apiClient.get<ApiResponse<CategoryDto[]>>(
      "/api/v1/categories",
    );
    return data.data;
  },
};
