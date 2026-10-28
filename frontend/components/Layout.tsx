import {
  Bell,
  ChevronDown,
  Gavel,
  LogOut,
  Menu,
  Search,
  Store,
  UserRound,
  Wallet,
} from "lucide-react";
import type { ReactNode } from "react";
import type { UserInfo } from "../types/auth";

export type AppPage = "home" | "auth" | "wallet";

interface LayoutProps {
  children: ReactNode;
  page: AppPage;
  setPage: (page: AppPage) => void;
  user: UserInfo | null;
  onRegister: () => void;
  onBecomeSeller: () => void;
  onSignOut: () => void;
}

function Brand({
  footer = false,
  onClick,
}: {
  footer?: boolean;
  onClick: () => void;
}) {
  return (
    <a
      className={`brand${footer ? " footer-brand" : ""}`}
      onClick={onClick}
      href="/"
    >
      <span className="brand-mark">
        <Gavel size={footer ? 15 : 17} />
      </span>
      <span>
        Auction<span className="brand-accent">Hub</span>
        {!footer && <small>THE ART OF THE BID</small>}
      </span>
    </a>
  );
}

function MainNavigation({
  page,
  setPage,
}: Pick<LayoutProps, "page" | "setPage">) {
  return (
    <nav className="main-nav">
      <a
        className={page === "home" ? "active" : ""}
        onClick={() => setPage("home")}
      >
        Khám phá
      </a>
    </nav>
  );
}

function HeaderActions({
  setPage,
  user,
  onRegister,
  onBecomeSeller,
  onSignOut,
}: Pick<LayoutProps, "setPage" | "user" | "onRegister" | "onBecomeSeller" | "onSignOut">) {
  return (
    <div className="top-actions">
      <button className="icon-btn" aria-label="T&#236;m ki&#7871;m"><Search size={17} /></button>
      <button className="icon-btn notification" aria-label="Th&#244;ng b&#225;o"><Bell size={17} /><i /></button>
      <span className="nav-divider" />
      <button className="wallet-nav" onClick={() => setPage("wallet")}><Wallet size={15} /> V&#237; c&#7911;a t&#244;i</button>
      <details className="profile-dropdown">
        <summary className="profile-trigger" aria-label={user ? "M&#7903; menu t&#224;i kho&#7843;n" : "&#272;&#259;ng nh&#7853;p ho&#7863;c &#273;&#259;ng k&#253;"}>
          <span className="avatar">{user ? user.fullName.charAt(0).toUpperCase() : <UserRound size={17} />}</span>
          <ChevronDown className="profile-chevron" size={13} />
        </summary>
        <div className="profile-menu">
          {user ? (
            <>
              <div className="profile-identity"><strong>{user.fullName}</strong><span>{user.email}</span><small>{user.role}</small></div>
              <button onClick={() => setPage("wallet")}><UserRound size={15} /> Th&#244;ng tin c&#225; nh&#226;n</button>
              {user.role !== "SELLER" && user.role !== "ADMIN" && (
                <button
                  onClick={(event) => {
                    event.currentTarget.closest("details")?.removeAttribute("open");
                    onBecomeSeller();
                  }}
                >
                  <Store size={15} /> Đăng ký trở thành seller
                </button>
              )}
              <button className="profile-signout" onClick={onSignOut}><LogOut size={15} /> &#272;&#259;ng xu&#7845;t</button>
            </>
          ) : (
            <>
              <div className="profile-identity"><strong>B&#7841;n ch&#432;a &#273;&#259;ng nh&#7853;p</strong><span>&#272;&#259;ng nh&#7853;p &#273;&#7875; tham gia &#273;&#7845;u gi&#225;.</span></div>
              <button className="profile-login" onClick={() => setPage("auth")}>&#272;&#259;ng nh&#7853;p</button>
              <button className="profile-register" onClick={onRegister}>T&#7841;o t&#224;i kho&#7843;n</button>
            </>
          )}
        </div>
      </details>
      <button className="mobile-menu" aria-label="M&#7903; tr&#236;nh &#273;&#417;n"><Menu /></button>
    </div>
  );
}
function Footer({ setPage }: Pick<LayoutProps, "setPage">) {
  return (
    <footer className="footer">
      <Brand footer onClick={() => setPage("home")} />
      <span>© 2025 AuctionHub · Đấu giá minh bạch, giao dịch an tâm.</span>
      <div>
        <a>Điều khoản</a>
        <a>Bảo mật</a>
        <a>Trợ giúp</a>
      </div>
    </footer>
  );
}

export function Layout({
  children,
  page,
  setPage,
  user,
  onRegister,
  onBecomeSeller,
  onSignOut,
}: LayoutProps) {
  return (
    <div className="app-shell">
      <header className="topbar">
        <Brand onClick={() => setPage("home")} />
        <MainNavigation page={page} setPage={setPage} />
        <HeaderActions
          setPage={setPage}
          user={user}
          onRegister={onRegister}
          onBecomeSeller={onBecomeSeller}
          onSignOut={onSignOut}
        />
      </header>
      <main>{children}</main>
      <Footer setPage={setPage} />
    </div>
  );
}
