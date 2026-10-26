import { ArrowUpRight, BadgeCheck, ShieldCheck, Sparkles } from "lucide-react";
import { formatVnd } from "../lib/format";
import type { AuctionProduct, ProductStatus } from "../types/auction";
import { CountdownTimer } from "./CountdownTimer";

function SellerBadge({ sellerName }: { sellerName?: string }) {
  return (
    <div className="seller">
      <span className="seller-avatar">A</span>
      <span>
        <small>NGƯỜI BÁN ĐÃ XÁC MINH</small>
        <b>
          {sellerName ?? "Nhà bán đã xác minh"} <BadgeCheck size={14} />
        </b>
      </span>
    </div>
  );
}

function AuctionCountdown({ endTime }: { endTime: string | null | undefined }) {
  return (
    <div className="countdown">
      <small>KẾT THÚC SAU</small>
      <CountdownTimer endTime={endTime ?? null} />
      <span>GIỜ&nbsp;&nbsp; PHÚT&nbsp;&nbsp; GIÂY</span>
    </div>
  );
}

interface FeaturedAuctionProps {
  item: AuctionProduct;
  onBid: (productId: number) => void;
}

const statusLabels: Record<ProductStatus, string> = {
  PENDING: "Ch\u1edd duy\u1ec7t",
  ACTIVE: "\u0110ang \u0111\u1ea5u gi\u00e1",
  SCHEDULED: "S\u1eafp di\u1ec5n ra",
  ENDED: "\u0110\u00e3 k\u1ebft th\u00fac",
  CANCELLED: "\u0110\u00e3 h\u1ee7y",
};

export function FeaturedAuction({ item, onBid }: FeaturedAuctionProps) {
  const priceIncrease = item.initialPrice
    ? (((item.price - item.initialPrice) / item.initialPrice) * 100).toFixed(1)
    : "12.4";
  const status = item.status ?? "ACTIVE";
  const isActive = status === "ACTIVE";

  return (
    <section className="hero">
      <div className="hero-photo">
        <img src={item.image} alt={item.title} />
        <div className="photo-shade" />
        <span className="feature-label">
          <Sparkles size={12} /> PHIÊN ĐẤU GIÁ ĐẶC BIỆT
        </span>
        <span className="photo-caption">{item.title}</span>
      </div>

      <div className="hero-panel">
        <div className={`eyebrow status-${status.toLowerCase()}`}>
          <i /> {statusLabels[status]} · #{item.id} · {item.category.toUpperCase()}
        </div>
        <h1>{item.title}</h1>
        <p className="hero-description">
          {item.description ??
            "Sản phẩm đã được xác minh, với thông tin minh bạch và bảo vệ thanh toán qua ký quỹ."}
        </p>
        <SellerBadge sellerName={item.sellerName} />

        <div className="hero-price">
          <div>
            <small>GIÁ HIỆN TẠI</small>
            <strong>{formatVnd(item.price)}</strong>
            <span className="up">↑ {priceIncrease}% so với giá khởi điểm</span>
          </div>
          <AuctionCountdown endTime={item.endTime} />
        </div>

        <button
          className="primary-button"
          onClick={() => onBid(item.id)}
          disabled={!isActive}
        >
          {isActive ? "Tham gia đấu giá" : statusLabels[status]}
          {isActive && <ArrowUpRight size={16} />}
        </button>
        <div className="trust-line">
          <ShieldCheck size={13} /> Tiền đặt cọc được bảo vệ · Hoàn tiền nếu không thắng
        </div>
      </div>
    </section>
  );
}
