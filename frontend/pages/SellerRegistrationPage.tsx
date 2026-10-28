import { useState, type ChangeEvent, type FormEvent } from "react";
import { ArrowLeft, Check, ImagePlus, ShieldCheck, UserRound } from "lucide-react";
import { useNavigate } from "react-router-dom";

function DocumentUpload({
  id,
  title,
  detail,
  file,
  onChange,
}: {
  id: string;
  title: string;
  detail: string;
  file: File | null;
  onChange: (file: File | null) => void;
}) {
  function handleChange(event: ChangeEvent<HTMLInputElement>) {
    onChange(event.target.files?.[0] ?? null);
  }

  return (
    <label className="seller-upload" htmlFor={id}>
      <span className="seller-upload-icon"><ImagePlus size={19} /></span>
      <span className="seller-upload-copy">
        <strong>{file ? file.name : title}</strong>
        <small>{file ? "Đã chọn ảnh · bấm để thay đổi" : detail}</small>
      </span>
      <span className="seller-upload-action">Chọn ảnh</span>
      <input
        id={id}
        type="file"
        accept="image/jpeg,image/png,image/webp"
        required
        onChange={handleChange}
      />
    </label>
  );
}

export function SellerRegistrationPage() {
  const navigate = useNavigate();
  const [frontId, setFrontId] = useState<File | null>(null);
  const [backId, setBackId] = useState<File | null>(null);
  const [selfie, setSelfie] = useState<File | null>(null);
  const [submitted, setSubmitted] = useState(false);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitted(true);
  }

  return (
    <section className="seller-register-page">
      <button className="seller-back" type="button" onClick={() => navigate(-1)}>
        <ArrowLeft size={15} /> Quay lại
      </button>

      <div className="seller-register-heading">
        <span className="seller-register-icon"><UserRound size={21} /></span>
        <div>
          <span className="seller-eyebrow">AUCTIONHUB · ĐỐI TÁC</span>
          <h1>Đăng ký trở thành seller</h1>
          <p>Hoàn tất thông tin xác minh để chuẩn bị bán hàng trên AuctionHub.</p>
        </div>
      </div>

      <form className="seller-register-card" onSubmit={handleSubmit}>
        <div className="seller-form-section-title">
          <span>01</span>
          <div>
          <h2>Thông tin cá nhân</h2>
            <p>Điền thông tin trùng khớp với giấy tờ xác minh.</p>
          </div>
        </div>

        <div className="seller-form-grid">
          <label className="seller-field">
            <span>Họ và tên thật</span>
            <input name="fullName" autoComplete="name" placeholder="Nhập họ và tên đầy đủ" required />
          </label>
          <label className="seller-field">
            <span>Số CCCD / CMND</span>
            <input
              name="identityNumber"
              inputMode="numeric"
              placeholder="Nhập số trên giấy tờ"
              required
            />
          </label>
          <label className="seller-field seller-field-wide">
            <span>Địa chỉ thường trú</span>
            <textarea name="address" rows={3} placeholder="Số nhà, đường, phường/xã, tỉnh/thành phố" required />
          </label>
          <label className="seller-field">
            <span>Ngân hàng</span>
            <select name="bankName" defaultValue="" required>
              <option value="" disabled>Chọn ngân hàng</option>
              <option value="vietcombank">Vietcombank</option>
              <option value="bidv">BIDV</option>
              <option value="vietinbank">VietinBank</option>
              <option value="agribank">Agribank</option>
              <option value="mb">MB Bank</option>
              <option value="techcombank">Techcombank</option>
              <option value="acb">ACB</option>
              <option value="vpbank">VPBank</option>
              <option value="sacombank">Sacombank</option>
              <option value="tpbank">TPBank</option>
              <option value="hdbank">HDBank</option>
              <option value="vib">VIB</option>
              <option value="ocb">OCB</option>
              <option value="msb">MSB</option>
              <option value="shb">SHB</option>
              <option value="seabank">SeABank</option>
              <option value="eximbank">Eximbank</option>
              <option value="lpbank">LPBank</option>
            </select>
          </label>
          <label className="seller-field">
            <span>Số tài khoản ngân hàng</span>
            <input
              name="bankAccountNumber"
              inputMode="numeric"
              autoComplete="off"
              placeholder="Nhập số tài khoản nhận thanh toán"
              required
            />
          </label>
        </div>

        <div className="seller-form-divider" />

        <div className="seller-form-section-title">
          <span>02</span>
          <div>
            <h2>Xác thực hình ảnh</h2>
            <p>Tải ảnh rõ nét, đủ sáng và không bị cắt góc.</p>
          </div>
        </div>

        <div className="seller-upload-grid">
          <DocumentUpload id="seller-id-front" title="Ảnh mặt trước CCCD" detail="JPG, PNG hoặc WEBP rõ nét" file={frontId} onChange={setFrontId} />
          <DocumentUpload id="seller-id-back" title="Ảnh mặt sau CCCD" detail="JPG, PNG hoặc WEBP rõ nét" file={backId} onChange={setBackId} />
          <DocumentUpload id="seller-selfie" title="Ảnh chân thực (selfie)" detail="Chụp rõ khuôn mặt, không dùng ảnh giấy tờ" file={selfie} onChange={setSelfie} />
        </div>

        <div className="seller-privacy-note">
          <ShieldCheck size={17} />
          <span>Ảnh giấy tờ và selfie hiện chỉ được chọn trong giao diện này, chưa được tải lên hay lưu ở đâu.</span>
        </div>

        <button className="seller-submit" type="submit">
          <Check size={16} /> Kiểm tra thông tin đăng ký
        </button>
        {submitted && (
          <p className="seller-submit-note" role="status">
            Biểu mẫu đã đủ thông tin. Chức năng gửi hồ sơ sẽ được kết nối sau.
          </p>
        )}
      </form>
    </section>
  );
}
