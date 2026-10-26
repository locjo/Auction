import { useCallback, useEffect, useRef, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ArrowLeft, ShieldCheck } from "lucide-react";
import { BiddingForm } from "../components/BiddingForm";
import { CountdownTimer } from "../components/CountdownTimer";
import { getApiErrorMessage } from "../lib/errors";
import { formatVnd } from "../lib/format";
import { connectToAuction } from "../service/auctionSocket";
import { bidService } from "../service/bidService";
import { mapProductDto, productService } from "../service/productService";
import { useAuctionStore } from "../stores/auctionStore";
import { useAuthStore } from "../stores/authStore";
import { useNotificationStore } from "../stores/notificationStore";
import type { AuctionProduct, AuctionUpdatePayload } from "../types/auction";

export function AuctionRoomPage() {
  const { id } = useParams();
  const productId = Number(id);
  const [product, setProduct] = useState<AuctionProduct | null>(null);
  const [loading, setLoading] = useState(true);
  const [bidLoading, setBidLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [bidError, setBidError] = useState<string | null>(null);
  const accessToken = useAuthStore((state) => state.accessToken);
  const applyUpdate = useAuctionStore((state) => state.applyUpdate);
  const pushNotice = useNotificationStore((state) => state.pushNotice);
  const seenBids = useRef(new Set<string>());

  useEffect(() => {
    let active = true;
    if (!Number.isSafeInteger(productId) || productId <= 0) {
      setError("Mã phiên đấu giá không hợp lệ.");
      setLoading(false);
      return;
    }

    productService
      .getById(productId)
      .then((result) => {
        if (active) setProduct(mapProductDto(result));
      })
      .catch((requestError: unknown) => {
        if (active) setError(getApiErrorMessage(requestError));
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [productId]);

  const handleUpdate = useCallback(
    (update: AuctionUpdatePayload) => {
      const eventKey = `${update.productId}:${update.createdAt}:${update.bidAmount}`;
      if (seenBids.current.has(eventKey)) return;
      seenBids.current.add(eventKey);
      applyUpdate(update);
      setProduct((current) =>
        current
          ? {
              ...current,
              price: Number(update.currentPrice),
              bids: current.bids + 1,
              endTime: update.endTime ?? current.endTime,
            }
          : current,
      );
    },
    [applyUpdate],
  );

  useEffect(() => {
    if (!product) return;
    return connectToAuction(product.id, handleUpdate, setBidError, accessToken);
  }, [accessToken, handleUpdate, product?.id]);

  async function submitBid(amount: number) {
    if (!product) return;
    setBidLoading(true);
    setBidError(null);
    try {
      const bid = await bidService.placeBid(product.id, { bidAmount: amount });
      handleUpdate({
        productId: bid.productId,
        currentPrice: bid.bidAmount,
        bidderName: bid.bidderName,
        bidAmount: bid.bidAmount,
        createdAt: bid.createdAt,
      });
      pushNotice("success", "Bạn đã trả giá thành công.");
    } catch (requestError: unknown) {
      setBidError(getApiErrorMessage(requestError));
    } finally {
      setBidLoading(false);
    }
  }

  if (loading) {
    return (
      <div className="page-wrap py-20 text-center text-slate-300">
        Đang tải phiên đấu giá...
      </div>
    );
  }
  if (error || !product) {
    return (
      <div className="page-wrap py-20 text-center">
        <p className="mb-4 text-rose-200">
          {error ?? "Không tìm thấy phiên đấu giá."}
        </p>
        <Link className="text-blue-300" to="/">
          Quay lại trang chủ
        </Link>
      </div>
    );
  }

  return (
    <div className="mx-auto grid max-w-6xl gap-6 px-1 py-8 lg:grid-cols-[1.4fr_0.8fr]">
      <section className="overflow-hidden rounded-xl border border-slate-700 bg-slate-900">
        <img
          className="h-72 w-full object-cover md:h-[28rem]"
          src={product.image}
          alt={product.title}
        />
        <div className="p-6">
          <Link
            className="mb-5 inline-flex items-center gap-2 text-sm text-blue-300"
            to="/"
          >
            <ArrowLeft size={16} /> Tất cả phiên đấu giá
          </Link>
          <p className="mb-2 text-xs uppercase tracking-widest text-blue-300">
            {product.category}
          </p>
          <h1 className="mb-3 font-serif text-3xl text-white">
            {product.title}
          </h1>
          <p className="text-sm text-slate-400">
            Người bán: {product.sellerName ?? "Đã xác minh"}
          </p>
          <p className="mt-6 flex items-center gap-2 text-sm text-emerald-200">
            <ShieldCheck size={16} /> Tiền đặt cọc được giữ an toàn trong tài
            khoản ký quỹ.
          </p>
        </div>
      </section>

      <aside className="flex flex-col gap-4">
        <div className="rounded-xl border border-amber-500/30 bg-slate-900 p-5">
          <p className="mb-2 text-xs uppercase tracking-widest text-slate-400">
            Kết thúc sau
          </p>
          <div className="font-mono text-3xl text-amber-300">
            <CountdownTimer endTime={product.endTime ?? null} />
          </div>
          <p className="mt-3 text-sm text-slate-400">
            Giá khởi điểm {formatVnd(product.initialPrice ?? product.price)}
          </p>
        </div>
        <BiddingForm
          currentPrice={product.price}
          loading={bidLoading}
          error={bidError}
          onSubmit={submitBid}
        />
        <div className="rounded-xl border border-slate-700 bg-slate-900 p-5 text-sm text-slate-300">
          <p className="font-semibold text-white">Cập nhật trực tiếp</p>
          <p className="mt-2 text-slate-400">
            Giá mới và lượt trả giá được đồng bộ qua WebSocket khi máy chủ phát
            sự kiện.
          </p>
        </div>
      </aside>
    </div>
  );
}
