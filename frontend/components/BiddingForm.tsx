import { useState, type FormEvent } from "react";
import { Gavel } from "lucide-react";
import { formatVnd } from "../lib/format";

interface BiddingFormProps {
  currentPrice: number;
  loading: boolean;
  error: string | null;
  onSubmit: (amount: number) => Promise<void>;
}

export function BiddingForm({
  currentPrice,
  loading,
  error,
  onSubmit,
}: BiddingFormProps) {
  const [amount, setAmount] = useState(currentPrice + 1);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await onSubmit(amount);
  }

  return (
    <form
      className="rounded-xl border border-slate-700 bg-slate-900 p-5"
      onSubmit={handleSubmit}
    >
      <p className="mb-1 text-xs uppercase tracking-widest text-slate-400">
        Giá hiện tại
      </p>
      <p className="mb-5 text-2xl font-semibold text-emerald-300">
        {formatVnd(currentPrice)}
      </p>
      <label className="mb-2 block text-sm text-slate-300" htmlFor="bidAmount">
        Giá trả của bạn
      </label>
      <input
        id="bidAmount"
        className="mb-2 w-full rounded-md border border-slate-700 bg-slate-950 px-3 py-3 text-white outline-none focus:border-blue-400"
        type="number"
        min={currentPrice + 1}
        step="any"
        value={amount}
        onChange={(event) => setAmount(Number(event.target.value))}
        required
      />
      <p className="mb-4 text-xs text-slate-500">
        Mức tối thiểu: {formatVnd(currentPrice + 1)}
      </p>
      {error && (
        <p
          className="mb-3 rounded-md border border-rose-500/30 bg-rose-950/30 p-3 text-sm text-rose-200"
          role="alert"
        >
          {error}
        </p>
      )}
      <button
        className="flex w-full items-center justify-center gap-2 rounded-md bg-blue-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-blue-500 disabled:cursor-wait disabled:opacity-60"
        disabled={loading || amount <= currentPrice}
        type="submit"
      >
        <Gavel size={16} /> {loading ? "Đang gửi giá..." : "Xác nhận trả giá"}
      </button>
    </form>
  );
}
