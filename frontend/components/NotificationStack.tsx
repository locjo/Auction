import { X } from "lucide-react";
import { useNotificationStore } from "../stores/notificationStore";

export function NotificationStack() {
  const notices = useNotificationStore((state) => state.notices);
  const dismissNotice = useNotificationStore((state) => state.dismissNotice);

  return (
    <div className="fixed right-4 top-20 z-50 flex w-[min(24rem,calc(100vw-2rem))] flex-col gap-2">
      {notices.map((notice) => (
        <div
          key={notice.id}
          role="status"
          className={`flex items-start justify-between gap-3 rounded-lg border bg-slate-900/95 p-3 text-sm shadow-xl ${
            notice.tone === "error"
              ? "border-rose-500/40 text-rose-100"
              : notice.tone === "success"
                ? "border-emerald-500/40 text-emerald-100"
                : "border-blue-500/40 text-blue-100"
          }`}
        >
          <span>{notice.message}</span>
          <button
            aria-label="Đóng thông báo"
            className="shrink-0 text-slate-400 hover:text-white"
            onClick={() => dismissNotice(notice.id)}
          >
            <X size={16} />
          </button>
        </div>
      ))}
    </div>
  );
}
