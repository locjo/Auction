import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { authService } from "../service/authService";
import { useAuthStore } from "../stores/authStore";

interface OAuth2RedirectPageProps {
  onSuccess: () => void;
}

export function OAuth2RedirectPage({ onSuccess }: OAuth2RedirectPageProps) {
  const location = useLocation();
  const navigate = useNavigate();
  const setSession = useAuthStore((state) => state.setSession);
  const [error, setError] = useState(false);

  useEffect(() => {
    let active = true;

    if (new URLSearchParams(location.search).has("error")) {
      setError(true);
      return () => {
        active = false;
      };
    }

    void authService
      .refresh()
      .then((session) => {
        if (!active) return;
        setSession(session);
        onSuccess();
      })
      .catch(() => {
        if (active) setError(true);
      });

    return () => {
      active = false;
    };
  }, [location.search, navigate, onSuccess, setSession]);

  return (
    <div className="oauth-redirect" role={error ? "alert" : "status"}>
      {error ? (
        <>
          <p>Đăng nhập Google chưa thành công.</p>
          <button className="google-button" onClick={() => navigate("/login")}>
            Quay lại đăng nhập
          </button>
        </>
      ) : (
        <p>Đang hoàn tất đăng nhập Google…</p>
      )}
    </div>
  );
}
