import { create } from "zustand";

export type NoticeTone = "success" | "error" | "info";

export interface AppNotice {
  id: string;
  tone: NoticeTone;
  message: string;
}

interface NotificationState {
  notices: AppNotice[];
  pushNotice: (tone: NoticeTone, message: string) => void;
  dismissNotice: (id: string) => void;
}

export const useNotificationStore = create<NotificationState>((set) => ({
  notices: [],
  pushNotice: (tone, message) =>
    set((state) => ({
      notices: [...state.notices, { id: crypto.randomUUID(), tone, message }],
    })),
  dismissNotice: (id) =>
    set((state) => ({
      notices: state.notices.filter((notice) => notice.id !== id),
    })),
}));
