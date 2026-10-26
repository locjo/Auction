import { Heart, Gavel, Clock3 } from "lucide-react";
import { formatVnd } from "../lib/format";
import { useAuctionStore } from "../stores/auctionStore";
import type { AuctionProduct, ProductStatus } from "../types/auction";

interface ProductCardProps {
  item: AuctionProduct;
  onBid: (productId: number) => void;
}

const statusLabels: Record<ProductStatus, string> = {
  PENDING: "Chờ duyệt",
  ACTIVE: "Đang đấu giá",
  SCHEDULED: "Sắp diễn ra",
  ENDED: "Đã kết thúc",
  CANCELLED: "Đã hủy",
};

export function ProductCard({ item, onBid }: ProductCardProps) {
  const liked = useAuctionStore((state) =>
    state.savedProductIds.includes(item.id),
  );
  const toggleSaved = useAuctionStore((state) => state.toggleSaved);
  const status = item.status ?? "ACTIVE";
  const isActive = status === "ACTIVE";

  return (
    <article className="product-card">
      <div className="product-image">
        <img src={item.image} alt={item.title} />
        <span className={`live-pill status-${status.toLowerCase()}`}>
          {isActive && <i />}
          {statusLabels[status]}
        </span>
        <button
          className={"heart " + (liked ? "liked" : "")}
          onClick={() => toggleSaved(item.id)}
          aria-label="Lưu sản phẩm"
        >
          <Heart size={16} fill={liked ? "currentColor" : "none"} />
        </button>
      </div>
      <div className="product-info">
        <div className="product-category">
          {item.category}
          <span>•</span> Đã xác thực
        </div>
        <h3>{item.title}</h3>
        <div className="product-meta">
          <span>
            <Gavel size={13} /> {item.bids} lượt đấu
          </span>
          <span>
            <Clock3 size={13} /> {item.time}
          </span>
        </div>
        <div className="price-row">
          <div>
            <small>GIÁ HIỆN TẠI</small>
            <strong>{formatVnd(item.price)}</strong>
          </div>
          <span className="bid-count">+{item.bids}</span>
        </div>
        {isActive ? (
          <button className="bid-button" onClick={() => onBid(item.id)}>
            <Gavel size={15} /> Đặt giá ngay <span>→</span>
          </button>
        ) : (
          <button className="bid-button" disabled>
            {statusLabels[status]}
          </button>
        )}
      </div>
    </article>
  );
}
