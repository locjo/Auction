import axios, { type AxiosError, type InternalAxiosRequestConfig } from "axios";
import type { ApiErrorBody, ApiResponse } from "../types/api";
import type { AuthResponse } from "../types/auth";
import { useAuthStore } from "../stores/authStore";

const configuredApiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? "/";
const apiBaseUrl = configuredApiBaseUrl.endsWith("/")
  ? configuredApiBaseUrl
  : `${configuredApiBaseUrl}/`;

export const apiClient = axios.create({
  baseURL: apiBaseUrl,
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = useAuthStore.getState().accessToken;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

let refreshRequest: Promise<AuthResponse | null> | null = null;

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ApiErrorBody>) => {
    const originalRequest = error.config as
      (InternalAxiosRequestConfig & { _retried?: boolean }) | undefined;
    const isAuthEndpoint = originalRequest?.url?.includes("/auth/");

    if (
      error.response?.status !== 401 ||
      !originalRequest ||
      originalRequest._retried ||
      isAuthEndpoint
    ) {
      return Promise.reject(error);
    }

    originalRequest._retried = true;
    refreshRequest ??= axios
      .post<ApiResponse<AuthResponse>>(
        `${apiBaseUrl}api/v1/auth/refresh`,
        {},
        { withCredentials: true },
      )
      .then(({ data }) => data.data)
      .catch(() => null)
      .finally(() => {
        refreshRequest = null;
      });

    const refreshed = await refreshRequest;
    if (!refreshed) {
      useAuthStore.getState().clearSession();
      return Promise.reject(error);
    }

    useAuthStore.getState().setSession(refreshed);
    originalRequest.headers.Authorization = `Bearer ${refreshed.accessToken}`;
    return apiClient(originalRequest);
  },
);
