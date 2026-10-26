import { CircleCheck, ShieldCheck, Sparkles } from "lucide-react";

const highlights = [
  {
    icon: CircleCheck,
    tone: "blue",
    title: "100% xác minh",
    description: "Mọi sản phẩm đều được thẩm định",
  },
  {
    icon: ShieldCheck,
    tone: "green",
    title: "Escrow bảo vệ",
    description: "Thanh toán an toàn tuyệt đối",
  },
  {
    icon: Sparkles,
    tone: "gold",
    title: "Đấu giá minh bạch",
    description: "Cập nhật giá theo thời gian thực",
  },
];

export function TrustHighlights() {
  return (
    <section className="quick-stats">
      {highlights.map(({ icon: Icon, tone, title, description }) => (
        <div key={title}>
          <span className={`stat-icon ${tone}`}>
            <Icon size={16} />
          </span>
          <span>
            <b>{title}</b>
            <small>{description}</small>
          </span>
        </div>
      ))}
      <div className="stats-total">
        <b>2.480+</b>
        <small>NHÀ SƯU TẦM TIN TƯỞNG</small>
      </div>
    </section>
  );
}
