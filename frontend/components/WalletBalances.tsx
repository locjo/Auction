import {
  ArrowDownToLine,
  ArrowUpFromLine,
  ChevronRight,
  ShieldCheck,
} from "lucide-react";
import type { WalletDto } from "../types/wallet";
import { formatVnd } from "../lib/format";

interface WalletBalancesProps {
  wallet: WalletDto | null;
  loading: boolean;
  preview: boolean;
}

function AvailableBalance({ wallet, loading, preview }: WalletBalancesProps) {
  return (
    <article className="balance-card main-balance">
      <div className="balance-top">
        <span>SỐ DƯ KHẢ DỤNG</span>
        <span className="verified">
          <ShieldCheck size={13} /> AN TOÀN
        </span>
      </div>
      <strong>
        {loading
          ? "Đang tải..."
          : wallet
            ? formatVnd(wallet.balance)
            : preview
              ? formatVnd(1_250_000)
              : "—"}
      </strong>
      <p>Sẵn sàng cho phiên đấu giá tiếp theo</p>
      <div className="balance-actions">
        <button className="primary-button">
          <ArrowDownToLine size={15} /> Nạp tiền
        </button>
        <button className="balance-secondary">
          <ArrowUpFromLine size={15} /> Rút tiền
        </button>
      </div>
      <div className="balance-foot">
        <span>●</span> Ví đã được xác minh danh tính
      </div>
    </article>
  );
}

function DepositBalance({ wallet, loading, preview }: WalletBalancesProps) {
  return (
    <article className="balance-card locked-balance">
      <div className="balance-top">
        <span>TIỀN ĐANG ĐẶT CỌC</span>
        <span className="deposit-icon">
          <ShieldCheck size={16} />
        </span>
      </div>
      <strong>
        {loading
          ? "Đang tải..."
          : wallet
            ? formatVnd(wallet.lockedBalance)
            : preview
              ? formatVnd(450_000)
              : "—"}
      </strong>
      <p>Được giữ an toàn trong tài khoản ký quỹ</p>
      <div className="deposit-progress">
        <span />
        <span />
        <span />
      </div>
      <small>
        ĐANG THAM GIA <b>2 PHIÊN</b>
      </small>
      <a>
        Xem chi tiết các phiên cọc <ChevronRight size={13} />
      </a>
    </article>
  );
}

function TotalBalance({ wallet, loading, preview }: WalletBalancesProps) {
  const total =
    Number(wallet?.balance ?? 1_250_000) +
    Number(wallet?.lockedBalance ?? 450_000);
  return (
    <article className="balance-card total-balance">
      <div className="balance-top">
        <span>TỔNG TÀI SẢN</span>
        <span className="total-icon">✦</span>
      </div>
      <strong>
        {loading ? "Đang tải..." : wallet || preview ? formatVnd(total) : "—"}
      </strong>
      <p>Số dư khả dụng + tiền đang đặt cọc</p>
      <div className="total-foot">
        <span>↗</span> Tăng <b>12.5%</b> tháng này
      </div>
    </article>
  );
}

export function WalletBalances({
  wallet,
  loading,
  preview,
}: WalletBalancesProps) {
  return (
    <section className="balance-grid">
      <AvailableBalance wallet={wallet} loading={loading} preview={preview} />
      <DepositBalance wallet={wallet} loading={loading} preview={preview} />
      <TotalBalance wallet={wallet} loading={loading} preview={preview} />
    </section>
  );
}
