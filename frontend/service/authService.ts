import { apiClient } from "../lib/apiClient";
import type { ApiResponse } from "../types/api";
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
} from "../types/auth";

export const authService = {
  async login(request: LoginRequest): Promise<AuthResponse> {
    const { data } = await apiClient.post<ApiResponse<AuthResponse>>(
      "/api/v1/auth/login",
      request,
    );
    return data.data;
  },

  async register(request: RegisterRequest): Promise<AuthResponse> {
    const { data } = await apiClient.post<ApiResponse<AuthResponse>>(
      "/api/v1/auth/register",
      request,
    );
    return data.data;
  },

  async refresh(): Promise<AuthResponse> {
    const { data } = await apiClient.post<ApiResponse<AuthResponse>>(
      "/api/v1/auth/refresh",
    );
    return data.data;
  },

  async logout(): Promise<void> {
    await apiClient.post<ApiResponse<null>>("/api/v1/auth/logout");
  },
};
