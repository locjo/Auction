import { useEffect, useState } from "react";
import { ChevronRight, ShieldCheck } from "lucide-react";
import { Breadcrumb } from "../components/Breadcrumb";
import { DepositPanel } from "../components/DepositPanel";
import { RecentActivity } from "../components/RecentActivity";
import { SectionHeading } from "../components/SectionHeading";
import { TransactionHistory } from "../components/TransactionHistory";
import { WalletBalances } from "../components/WalletBalances";
import { getApiErrorMessage } from "../lib/errors";
import { walletService } from "../service/walletService";
import type { WalletDto } from "../types/wallet";
import type { UserInfo } from "../types/auth";

function PreviewNotice({ onSignIn }: { onSignIn: () => void }) {
  return (
    <div className="notice">
      <ShieldCheck size={17} /> Bạn đang xem bản xem trước.
      <button onClick={onSignIn}>
        Đăng nhập để sử dụng ví <ChevronRight size={14} />
      </button>
    </div>
  );
}

interface WalletPageProps {
  user: UserInfo | null;
  onSignIn: () => void;
}

export function WalletPage({ user, onSignIn }: WalletPageProps) {
  const [wallet, setWallet] = useState<WalletDto | null>(null);
  const [loading, setLoading] = useState(Boolean(user));
  const [loadError, setLoadError] = useState<string | null>(null);

  useEffect(() => {
    if (!user) {
      setLoading(false);
      return;
    }

    let mounted = true;
    setLoading(true);
    walletService
      .getMine()
      .then((result) => {
        if (mounted) setWallet(result);
      })
      .catch((error: unknown) => {
        if (mounted) setLoadError(getApiErrorMessage(error));
      })
      .finally(() => {
        if (mounted) setLoading(false);
      });

    return () => {
      mounted = false;
    };
  }, [user]);

  return (
    <div className="page-wrap wallet-page">
      <Breadcrumb current="VÍ & ĐẶT CỌC" />
      {!user && <PreviewNotice onSignIn={onSignIn} />}

      <SectionHeading
        className="wallet-heading"
        eyebrow="QUẢN LÝ TÀI CHÍNH"
        title="Ví & đặt cọc"
        emphasis="đấu giá"
        description="Theo dõi số dư, quản lý tiền cọc và lịch sử giao dịch của bạn."
        action={<button className="outline-button">Lịch sử giao dịch</button>}
      />

      <WalletBalances wallet={wallet} loading={loading} preview={!user} />
      {loadError && (
        <div
          className="rounded border border-rose-500/40 bg-rose-950/30 px-3 py-2 text-xs text-rose-200"
          role="alert"
        >
          {loadError}
        </div>
      )}

      <div className="wallet-content">
        <DepositPanel />
        <RecentActivity />
      </div>

      <TransactionHistory />
    </div>
  );
}
