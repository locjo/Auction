import { useCallback, useEffect, useState } from "react";
import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
  useLocation,
  useNavigate,
} from "react-router-dom";
import { Layout, type AppPage } from "../components/Layout";
import { NotificationStack } from "../components/NotificationStack";
import { AuthPage } from "../pages/AuthPage";
import { HomePage } from "../pages/HomePage";
import { WalletPage } from "../pages/WalletPage";
import { AuctionRoomPage } from "../pages/AuctionRoomPage";
import { OAuth2RedirectPage } from "../pages/OAuth2RedirectPage";
import { SellerRegistrationPage } from "../pages/SellerRegistrationPage";
import { AdminUsersPage } from "../pages/AdminUsersPage";
import { authService } from "../service/authService";
import { useAuthStore } from "../stores/authStore";

function AuctionApp() {
  const navigate = useNavigate();
  const location = useLocation();
  const user = useAuthStore((state) => state.user);
  const setSession = useAuthStore((state) => state.setSession);
  const clearSession = useAuthStore((state) => state.clearSession);
  const [sessionReady, setSessionReady] = useState(false);
  const page: AppPage = location.pathname.startsWith("/admin")
    ? "admin"
    : location.pathname.startsWith("/wallet")
    ? "wallet"
    : location.pathname.startsWith("/login") ||
        location.pathname.startsWith("/register")
      ? "auth"
      : "home";

  useEffect(() => {
    void authService
      .refresh()
      .then(setSession)
      .catch(() => undefined)
      .finally(() => setSessionReady(true));
  }, [setSession]);

  function setPage(nextPage: AppPage) {
    const routes: Record<AppPage, string> = {
      home: "/",
      auth: "/login",
      wallet: "/wallet",
      admin: "/admin/users",
    };
    navigate(routes[nextPage]);
  }

  function openAuction(productId: number) {
    navigate(
      user
        ? `/auctions/${productId}`
        : `/login?redirect=/auctions/${productId}`,
    );
  }

  const finishAuthentication = useCallback(() => {
    const redirect = new URLSearchParams(location.search).get("redirect");
    const isAuction = redirect?.startsWith("/auctions/");
    const isAdmin = redirect === "/admin/users" && useAuthStore.getState().user?.role === "ADMIN";
    const destination = isAuction || isAdmin ? redirect : null;
    navigate(destination ?? "/wallet");
  }, [location.search, navigate]);

  function signOut() {
    clearSession();
    navigate("/");
    void authService.logout().catch(() => undefined);
  }

  return (
    <Layout
      page={page}
      setPage={setPage}
      user={user}
      onRegister={() => navigate("/register")}
      onBecomeSeller={() => navigate("/seller/register")}
      onSignOut={signOut}
    >
      <Routes>
        <Route
          path="/oauth2/redirect"
          element={<OAuth2RedirectPage onSuccess={finishAuthentication} />}
        />
        <Route path="/seller/register" element={<SellerRegistrationPage />} />
        <Route path="/admin/users" element={sessionReady ? <AdminUsersPage user={user} /> : <div className="admin-loading">Đang kiểm tra phiên đăng nhập…</div>} />
        <Route path="/" element={<HomePage onSignIn={openAuction} />} />
        <Route path="/auctions/:id" element={<AuctionRoomPage />} />
        <Route
          path="/login"
          element={
            <AuthPage initialSignup={false} onSuccess={finishAuthentication} />
          }
        />
        <Route
          path="/register"
          element={
            <AuthPage initialSignup onSuccess={() => setPage("wallet")} />
          }
        />
        <Route
          path="/wallet"
          element={<WalletPage user={user} onSignIn={() => setPage("auth")} />}
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
      <NotificationStack />
    </Layout>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuctionApp />
    </BrowserRouter>
  );
}
