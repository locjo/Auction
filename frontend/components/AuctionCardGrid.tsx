import { ChevronRight } from "lucide-react";
import type { AuctionProduct } from "../types/auction";
import { ProductCard } from "./ProductCard";

interface AuctionCardGridProps {
  products: AuctionProduct[];
  loading: boolean;
  onBid: (productId: number) => void;
}

export function AuctionCardGrid({
  products,
  loading,
  onBid,
}: AuctionCardGridProps) {
  if (loading) {
    return (
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: 6 }, (_, index) => (
          <div
            className="h-72 animate-pulse rounded-lg border border-slate-700 bg-slate-800/70"
            key={index}
          />
        ))}
      </div>
    );
  }

  if (products.length === 0) {
    return (
      <div className="empty-state">
        Không tìm thấy phiên phù hợp. Thử một từ khóa khác nhé.
      </div>
    );
  }

  return (
    <>
      <div className="product-grid">
        {products.map((product) => (
          <ProductCard key={product.id} item={product} onBid={onBid} />
        ))}
      </div>
      <button className="load-more">
        Khám phá tất cả phiên đấu giá <ChevronRight size={15} />
      </button>
    </>
  );
}
