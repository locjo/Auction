export type UserStatus = "ACTIVE" | "LOCKED" | "PENDING";
export type UserRole = "ADMIN" | "SELLER" | "BIDDER";
export type KycStatus = "NOT_SUBMITTED" | "PENDING" | "APPROVED" | "REJECTED";

export interface AdminUser {
  id: number;
  email: string;
  fullName: string;
  phone: string | null;
  role: UserRole;
  status: UserStatus;
  kycStatus: KycStatus;
  identityNumber: string | null;
  identityDocumentUrl: string | null;
  phoneVerified: boolean;
  emailVerified: boolean;
  kycRejectionReason: string | null;
  createdAt: string;
}

export interface UserPage {
  content: AdminUser[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
