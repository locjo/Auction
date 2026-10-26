import {
  ArrowDownToLine,
  CreditCard,
  Plus,
  QrCode,
  ShieldCheck,
} from "lucide-react";

const amounts = ["₫ 500.000", "₫ 1.000.000", "₫ 2.000.000", "Khác"];

function AmountSelector() {
  return (
    <>
      <div className="deposit-amount-label">CHỌN SỐ TIỀN NẠP</div>
      <div className="amount-options">
        {amounts.map((amount, index) => (
          <button className={index === 1 ? "active" : ""} key={amount}>
            {index === 3 && <Plus size={14} />}
            {amount}
          </button>
        ))}
      </div>
    </>
  );
}

function PaymentMethodSelector() {
  return (
    <div className="payment-methods">
      <button className="method active">
        <span className="vnpay-logo">
          VN<span>Pay</span>
        </span>
        <span>
          <b>VNPay</b>
          <small>Thẻ ATM / QR Code</small>
        </span>
        <i />
      </button>
      <button className="method">
        <span className="bank-logo">
          <CreditCard />
        </span>
        <span>
          <b>Chuyển khoản ngân hàng</b>
          <small>Miễn phí · xử lý trong 5 phút</small>
        </span>
        <i />
      </button>
    </div>
  );
}

function QrPaymentInfo() {
  return (
    <div className="qr-instructions">
      <div className="qr-placeholder">
        <QrCode size={77} />
        <span>VNPay QR</span>
      </div>
      <div>
        <b>Quét mã để thanh toán</b>
        <p>
          Mở ứng dụng ngân hàng hoặc ví điện tử và quét mã QR. Giao dịch sẽ được
          cập nhật tự động.
        </p>
        <small>
          ● Mã có hiệu lực trong <b>14:52</b>
        </small>
      </div>
    </div>
  );
}

export function DepositPanel() {
  return (
    <section className="panel deposit-panel">
      <div className="panel-heading">
        <div>
          <h3>Nạp tiền vào ví</h3>
          <p>Nạp nhanh qua VNPay hoặc chuyển khoản ngân hàng.</p>
        </div>
        <span className="secure-tag">
          <ShieldCheck size={13} /> BẢO MẬT
        </span>
      </div>
      <AmountSelector />
      <PaymentMethodSelector />
      <QrPaymentInfo />
      <button className="primary-button full-button">
        Tạo mã thanh toán <ArrowDownToLine size={15} />
      </button>
      <div className="fineprint">
        <ShieldCheck size={12} /> Giao dịch được mã hóa và bảo vệ bởi VNPay.
      </div>
    </section>
  );
}
