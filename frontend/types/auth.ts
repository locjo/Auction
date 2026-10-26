export interface UserInfo {
  id: number;
  email: string;
  fullName: string;
  role: string;
}

export interface AuthResponse {
  tokenType: "Bearer" | string;
  accessToken: string;
  accessTokenExpiresAt: string;
  user: UserInfo;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest extends LoginRequest {
  fullName: string;
  phone?: string;
}
