import { ChevronRight, ShieldCheck } from "lucide-react";

const activities = [
  [
    "↓",
    "Nạp tiền thành công",
    "VNPay · hôm nay, 09:42",
    "+ ₫ 1.000.000",
    "green",
  ],
  [
    "♙",
    "Đặt cọc phiên đấu giá",
    "Rolex Submariner · 09:18",
    "− ₫ 250.000",
    "gold",
  ],
  ["↗", "Hoàn tiền đặt cọc", "Leica M6 TTL · hôm qua", "+ ₫ 150.000", "green"],
  ["↓", "Nạp tiền thành công", "VNPay · 21/06/2025", "+ ₫ 500.000", "green"],
];

function ActivityRow({ activity }: { activity: (typeof activities)[number] }) {
  const [icon, title, detail, amount, tone] = activity;
  return (
    <div className="activity-row">
      <span className={`activity-icon ${tone}`}>{icon}</span>
      <span>
        <b>{title}</b>
        <small>{detail}</small>
      </span>
      <strong className={tone}>{amount}</strong>
    </div>
  );
}

function EscrowNotice() {
  return (
    <div className="escrow-note">
      <ShieldCheck />
      <span>
        <b>Tiền cọc của bạn luôn an toàn</b>
        <small>
          Tiền chỉ được chuyển cho người bán sau khi bạn xác nhận nhận hàng.
        </small>
      </span>
    </div>
  );
}

export function RecentActivity() {
  return (
    <aside className="panel activity-panel">
      <div className="panel-heading">
        <div>
          <h3>Hoạt động gần đây</h3>
          <p>Cập nhật giao dịch mới nhất của bạn.</p>
        </div>
        <a>
          Xem tất cả <ChevronRight size={13} />
        </a>
      </div>
      {activities.map((activity) => (
        <ActivityRow activity={activity} key={activity[1] + activity[2]} />
      ))}
      <EscrowNotice />
    </aside>
  );
}
