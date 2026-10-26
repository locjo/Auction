import {
  useState,
  type FormEvent,
  type InputHTMLAttributes,
  type ComponentType,
} from "react";
import {
  ArrowRight,
  Gavel,
  LockKeyhole,
  Mail,
  ShieldCheck,
  UserRound,
} from "lucide-react";
import { getApiErrorMessage } from "../lib/errors";
import { authService } from "../service/authService";
import { useAuthStore } from "../stores/authStore";

interface FormFieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  icon: ComponentType<{ size?: number }>;
}

function FormField({ label, icon: Icon, ...inputProps }: FormFieldProps) {
  return (
    <label className="form-label">
      {label}
      <div>
        <Icon size={14} />
        <input {...inputProps} />
      </div>
    </label>
  );
}

interface AuthFormProps {
  initialSignup: boolean;
  onSuccess: () => void;
  onModeChange: (signup: boolean) => void;
}

export function AuthForm({
  initialSignup,
  onSuccess,
  onModeChange,
}: AuthFormProps) {
  const [signup, setSignup] = useState(initialSignup);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const setSession = useAuthStore((state) => state.setSession);

  function changeMode(nextMode: boolean) {
    setSignup(nextMode);
    setError(null);
    onModeChange(nextMode);
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const formData = new FormData(event.currentTarget);
    const email = String(formData.get("email") ?? "");
    const password = String(formData.get("password") ?? "");

    try {
      const session = signup
        ? await authService.register({
            email,
            password,
            fullName: String(formData.get("fullName") ?? ""),
            phone: String(formData.get("phone") ?? ""),
          })
        : await authService.login({ email, password });
      setSession(session);
      onSuccess();
    } catch (requestError: unknown) {
      setError(getApiErrorMessage(requestError));
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-card">
      <div className="auth-card-head">
        <span className="auth-icon">
          <Gavel />
        </span>
        <span>
          AUCTIONHUB
          <small>THỊ TRƯỜNG ĐẤU GIÁ TRỰC TUYẾN</small>
        </span>
      </div>

      <h2>{signup ? "Tạo tài khoản" : "Chào mừng trở lại"}</h2>
      <p>
        {signup
          ? "Bắt đầu hành trình sưu tầm của bạn."
          : "Đăng nhập để tiếp tục khám phá các phiên đấu giá."}
      </p>

      <div className="auth-switch">
        <button
          className={!signup ? "on" : ""}
          onClick={() => changeMode(false)}
        >
          Đăng nhập
        </button>
        <button className={signup ? "on" : ""} onClick={() => changeMode(true)}>
          Tạo tài khoản
        </button>
      </div>

      <form onSubmit={handleSubmit}>
        {signup && (
          <>
            <FormField
              label="Họ và tên"
              icon={UserRound}
              name="fullName"
              placeholder="Tên hiển thị của bạn"
              maxLength={100}
              required
            />
            <FormField
              label="Số điện thoại (không bắt buộc)"
              icon={UserRound}
              name="phone"
              placeholder="090..."
              maxLength={20}
            />
          </>
        )}
        <FormField
          label="Địa chỉ email"
          icon={Mail}
          name="email"
          type="email"
          placeholder="you@example.com"
          autoComplete="email"
          required
        />
        <FormField
          label="Mật khẩu"
          icon={LockKeyhole}
          name="password"
          type="password"
          placeholder="Tối thiểu 8 ký tự"
          minLength={signup ? 8 : undefined}
          autoComplete={signup ? "new-password" : "current-password"}
          required
        />

        {!signup && <a className="forgot">Quên mật khẩu?</a>}
        {error && (
          <div
            className="mb-3 rounded border border-rose-500/40 bg-rose-950/30 px-3 py-2 text-xs text-rose-200"
            role="alert"
          >
            {error}
          </div>
        )}
        <button
          className="primary-button auth-submit"
          type="submit"
          disabled={loading}
        >
          {loading ? "Đang xử lý..." : signup ? "Tạo tài khoản" : "Đăng nhập"}
          {!loading && <ArrowRight size={16} />}
        </button>
      </form>

      <div className="auth-safe">
        <ShieldCheck size={14} /> Thông tin của bạn luôn được mã hóa và bảo mật.
      </div>
    </section>
  );
}
