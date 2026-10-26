import { create } from "zustand";
import type { AuthResponse, UserInfo } from "../types/auth";

interface AuthState {
  accessToken: string | null;
  user: UserInfo | null;
  setSession: (session: AuthResponse) => void;
  clearSession: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  user: null,
  setSession: ({ accessToken, user }) => set({ accessToken, user }),
  clearSession: () => set({ accessToken: null, user: null }),
}));
