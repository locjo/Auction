import { apiClient } from "../lib/apiClient";
import type { ApiResponse } from "../types/api";
import type { KycStatus, UserPage, UserRole, UserStatus } from "../types/admin";

export interface UserFilters {
  q?: string;
  status?: UserStatus | "";
  role?: UserRole | "";
  kycStatus?: KycStatus | "";
  page?: number;
  size?: number;
}

export const adminUserService = {
  async list(filters: UserFilters = {}) {
    const { data } = await apiClient.get<ApiResponse<UserPage>>("api/v1/admin/users", { params: filters });
    return data.data;
  },
  async reviewKyc(id: number, decision: "approve" | "reject", reason?: string) {
    await apiClient.patch(`api/v1/admin/users/${id}/kyc/${decision}`, decision === "reject" ? { reason } : undefined);
  },
  async approveSeller(id: number, reason: string) {
    await apiClient.patch(`api/v1/admin/users/${id}/seller-approval`, { reason });
  },
  async setLocked(id: number, locked: boolean, reason: string) {
    await apiClient.patch(`api/v1/admin/users/${id}/${locked ? "lock" : "unlock"}`, { reason });
  },
  async warn(id: number, reason: string) {
    await apiClient.post(`api/v1/admin/users/${id}/warnings`, { reason });
  },
};
