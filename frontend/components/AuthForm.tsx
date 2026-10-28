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

      {!signup && (
        <>
          <div className="auth-divider"><span>hoặc</span></div>
          <button
            className="google-button"
            type="button"
            onClick={() => {
              window.location.assign("/oauth2/authorization/google");
            }}
          >
            <svg aria-hidden="true" viewBox="0 0 48 48" width="17" height="17">
              <path fill="#4285F4" d="M43.6 24.5c0-1.4-.1-2.8-.4-4.1H24v7.8h11a9.4 9.4 0 0 1-4.1 6.2l6.6 5.1c3.9-3.6 6.1-8.8 6.1-15Z" />
              <path fill="#34A853" d="M24 44c5.5 0 10.2-1.8 13.5-4.9l-6.6-5.1c-1.8 1.2-4 2-6.9 2-5.3 0-9.8-3.6-11.4-8.4l-6.8 5.2A20 20 0 0 0 24 44Z" />
              <path fill="#FBBC05" d="M12.6 27.6a12 12 0 0 1 0-7.2l-6.8-5.2a20 20 0 0 0 0 17.6l6.8-5.2Z" />
              <path fill="#EA4335" d="M24 12c3 0 5.7 1 7.8 3.1l5.8-5.8C34.1 6 29.5 4 24 4A20 20 0 0 0 5.8 15.2l6.8 5.2C14.2 15.6 18.7 12 24 12Z" />
            </svg>
            Đăng nhập bằng Google
          </button>
        </>
      )}

      <div className="auth-safe">
        <ShieldCheck size={14} /> Thông tin của bạn luôn được mã hóa và bảo mật.
      </div>
    </section>
  );
}
