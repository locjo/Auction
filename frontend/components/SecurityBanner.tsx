import { ArrowUpRight, ShieldCheck } from "lucide-react";

const protections = [
  [
    "01",
    "Thẩm định độc lập",
    "Chuyên gia kiểm tra từng sản phẩm trước khi lên sàn.",
  ],
  [
    "02",
    "Thanh toán ký quỹ",
    "Tiền chỉ được chuyển khi bạn xác nhận nhận hàng.",
  ],
  [
    "03",
    "Hoàn tiền minh bạch",
    "Đặt cọc được hoàn lại tự động nếu bạn không thắng.",
  ],
];

export function SecurityBanner() {
  return (
    <section className="security-banner">
      <div className="security-emblem">
        <ShieldCheck size={29} />
      </div>
      <div className="security-copy">
        <div className="eyebrow">CAM KẾT TỪ AUCTIONHUB</div>
        <h2>
          Đấu giá tự tin.
          <br />
          <em>An tâm tuyệt đối.</em>
        </h2>
        <p>
          Mỗi giao dịch được bảo vệ bởi hệ thống ký quỹ và đội ngũ thẩm định độc
          lập của chúng tôi.
        </p>
        <a>
          Tìm hiểu về bảo vệ người mua <ArrowUpRight size={14} />
        </a>
      </div>
      <div className="security-points">
        {protections.map(([number, title, description]) => (
          <div key={number}>
            <span>{number}</span>
            <span>
              <b>{title}</b>
              <small>{description}</small>
            </span>
          </div>
        ))}
      </div>
    </section>
  );
}
