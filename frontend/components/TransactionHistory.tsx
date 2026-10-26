import { Clock3 } from "lucide-react";

const transactions = [
  [
    "Nạp tiền vào ví",
    "TXN-847291",
    "22/06/2025 · 09:42",
    "VNPay QR",
    "+ ₫ 1.000.000",
    "Thành công",
  ],
  [
    "Đặt cọc đấu giá",
    "BID-384729",
    "22/06/2025 · 09:18",
    "Ví AuctionHub",
    "− ₫ 250.000",
    "Đang giữ",
  ],
  [
    "Hoàn tiền đặt cọc",
    "REF-284612",
    "21/06/2025 · 16:05",
    "Ví AuctionHub",
    "+ ₫ 150.000",
    "Hoàn tất",
  ],
  [
    "Nạp tiền vào ví",
    "TXN-721048",
    "21/06/2025 · 11:26",
    "Chuyển khoản",
    "+ ₫ 500.000",
    "Thành công",
  ],
];

const filters = ["Tất cả", "Nạp tiền", "Đặt cọc", "Hoàn tiền", "Thanh toán"];

function TransactionFilters() {
  return (
    <div className="transaction-filters">
      {filters.map((filter, index) => (
        <button className={index === 0 ? "on" : ""} key={filter}>
          {filter}
        </button>
      ))}
    </div>
  );
}

function TransactionTable() {
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>GIAO DỊCH</th>
            <th>MÃ THAM CHIẾU</th>
            <th>NGÀY THÁNG</th>
            <th>PHƯƠNG THỨC</th>
            <th>SỐ TIỀN</th>
            <th>TRẠNG THÁI</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((transaction, index) => (
            <TransactionRow
              key={transaction[1]}
              transaction={transaction}
              isDeposit={index !== 1}
            />
          ))}
        </tbody>
      </table>
    </div>
  );
}

function TransactionRow({
  transaction,
  isDeposit,
}: {
  transaction: (typeof transactions)[number];
  isDeposit: boolean;
}) {
  const [name, reference, date, method, amount, status] = transaction;
  return (
    <tr>
      <td>
        <span className="table-icon">{isDeposit ? "↗" : "♙"}</span>
        {name}
      </td>
      <td>{reference}</td>
      <td>{date}</td>
      <td>{method}</td>
      <td className={amount.startsWith("+") ? "positive" : "negative"}>
        {amount}
      </td>
      <td className="status-cell">{status}</td>
    </tr>
  );
}

function Pagination() {
  return (
    <div className="pagination">
      <span>Hiển thị 1–4 trong 24 giao dịch</span>
      <div>
        <button aria-label="Trang trước">‹</button>
        <button className="current">1</button>
        <button>2</button>
        <button>3</button>
        <button>…</button>
        <button>6</button>
        <button aria-label="Trang sau">›</button>
      </div>
    </div>
  );
}

export function TransactionHistory() {
  return (
    <section className="panel transactions">
      <div className="panel-heading">
        <div>
          <h3>Lịch sử giao dịch</h3>
          <p>Toàn bộ hoạt động trong ví của bạn.</p>
        </div>
        <button className="filter-button">
          <Clock3 size={14} /> 30 ngày qua
        </button>
      </div>
      <TransactionFilters />
      <TransactionTable />
      <Pagination />
    </section>
  );
}
