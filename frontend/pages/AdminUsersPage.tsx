import { useCallback, useEffect, useState, type FormEvent } from "react";
import { Navigate } from "react-router-dom";
import { AlertCircle, ArrowLeft, ArrowRight, Ban, Check, CheckCircle2, ChevronDown, Clock3, Eye, FileCheck2, LockKeyhole, RefreshCw, Search, ShieldAlert, ShieldCheck, UnlockKeyhole, UserRound, Users, X } from "lucide-react";
import { adminUserService } from "../service/adminUserService";
import type { AdminUser, KycStatus, UserRole, UserStatus } from "../types/admin";
import type { UserInfo } from "../types/auth";

type Action = "reject-kyc" | "seller" | "lock" | "unlock" | "warn";

const kycLabels: Record<KycStatus, string> = {
  NOT_SUBMITTED: "Chưa gửi",
  PENDING: "Chờ duyệt",
  APPROVED: "Đã xác minh",
  REJECTED: "Cần bổ sung",
};
const statusLabels: Record<UserStatus, string> = { ACTIVE: "Hoạt động", LOCKED: "Đã khóa", PENDING: "Chờ xử lý" };
const roleLabels: Record<UserRole, string> = { ADMIN: "Admin", SELLER: "Seller", BIDDER: "Bidder" };

function userInitials(name: string) {
  return name.trim().split(/\s+/).slice(-2).map((part) => part[0]?.toUpperCase()).join("");
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat("vi-VN", { dateStyle: "medium" }).format(new Date(value));
}

export function AdminUsersPage({ user }: { user: UserInfo | null }) {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [query, setQuery] = useState("");
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState<UserStatus | "">("");
  const [role, setRole] = useState<UserRole | "">("");
  const [kycStatus, setKycStatus] = useState<KycStatus | "">("");
  const [pendingKyc, setPendingKyc] = useState(0);
  const [lockedUsers, setLockedUsers] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [selectedUser, setSelectedUser] = useState<AdminUser | null>(null);
  const [action, setAction] = useState<Action | null>(null);
  const [reason, setReason] = useState("");
  const [saving, setSaving] = useState(false);

  const loadUsers = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [result, pendingResult, lockedResult] = await Promise.all([
        adminUserService.list({ q: search || undefined, status: status || undefined, role: role || undefined, kycStatus: kycStatus || undefined, page, size: 10 }),
        adminUserService.list({ kycStatus: "PENDING", page: 0, size: 1 }),
        adminUserService.list({ status: "LOCKED", page: 0, size: 1 }),
      ]);
      setUsers(result.content);
      setTotal(result.totalElements);
      setTotalPages(result.totalPages);
      setPendingKyc(pendingResult.totalElements);
      setLockedUsers(lockedResult.totalElements);
      setSelectedUser((current) => current ? result.content.find((item) => item.id === current.id) ?? null : null);
    } catch {
      setError("Không thể tải dữ liệu người dùng. Hãy kiểm tra kết nối và quyền ADMIN.");
    } finally {
      setLoading(false);
    }
  }, [search, status, role, kycStatus, page]);

  useEffect(() => { void loadUsers(); }, [loadUsers]);

  if (!user) return <Navigate to="/login?redirect=/admin/users" replace />;
  if (user.role !== "ADMIN") return <Navigate to="/" replace />;

  function submitSearch(event: FormEvent) {
    event.preventDefault();
    setPage(0);
    setSearch(query.trim());
  }

  function clearFilters() {
    setQuery(""); setSearch(""); setStatus(""); setRole(""); setKycStatus(""); setPage(0);
  }

  function startAction(target: AdminUser, nextAction: Action) {
    setSelectedUser(target);
    setAction(nextAction);
    setReason("");
  }

  async function runAction(event: FormEvent) {
    event.preventDefault();
    if (!selectedUser || !action || reason.trim().length < 3) return;
    setSaving(true);
    try {
      if (action === "reject-kyc") await adminUserService.reviewKyc(selectedUser.id, "reject", reason.trim());
      if (action === "seller") await adminUserService.approveSeller(selectedUser.id, reason.trim());
      if (action === "lock") await adminUserService.setLocked(selectedUser.id, true, reason.trim());
      if (action === "unlock") await adminUserService.setLocked(selectedUser.id, false, reason.trim());
      if (action === "warn") await adminUserService.warn(selectedUser.id, reason.trim());
      const message: Record<Action, string> = {
        "reject-kyc": "Đã từ chối hồ sơ KYC.", seller: "Đã phê duyệt quyền Seller.",
        lock: "Đã khóa tài khoản.", unlock: "Đã mở khóa tài khoản.", warn: "Đã ghi nhận cảnh cáo.",
      };
      setNotice(message[action]);
      setAction(null);
      await loadUsers();
    } catch {
      setError("Thao tác chưa thành công. Trạng thái có thể đã thay đổi; hãy tải lại dữ liệu.");
    } finally {
      setSaving(false);
    }
  }

  async function approveKyc(target: AdminUser) {
    if (!window.confirm(`Xác nhận đã kiểm tra giấy tờ và thông tin liên hệ của ${target.fullName}?`)) return;
    try {
      await adminUserService.reviewKyc(target.id, "approve");
      setNotice("Đã xác minh KYC.");
      await loadUsers();
    } catch {
      setError("Không thể duyệt KYC. Kiểm tra lại hồ sơ và trạng thái tài khoản.");
    }
  }

  const modalTitle: Record<Action, string> = {
    "reject-kyc": "Từ chối hồ sơ KYC", seller: "Phê duyệt quyền Seller", lock: "Khóa tài khoản",
    unlock: "Mở khóa tài khoản", warn: "Ghi nhận cảnh cáo",
  };

  return (
    <section className="admin-page">
      <div className="admin-heading">
        <div>
          <div className="admin-eyebrow"><ShieldCheck size={14} /> TRUNG TÂM QUẢN TRỊ</div>
          <h1>Quản lý người dùng</h1>
          <p>Duyệt danh tính, quản lý quyền Seller và xử lý tài khoản trên AuctionHub.</p>
        </div>
        <button className="admin-refresh" onClick={() => void loadUsers()} disabled={loading}><RefreshCw size={15} className={loading ? "spin" : ""} /> Làm mới</button>
      </div>

      <div className="admin-stat-grid">
        <article className="admin-stat"><span className="admin-stat-icon blue"><Users size={18} /></span><div><small>Tổng kết quả</small><strong>{total.toLocaleString("vi-VN")}</strong><span>theo bộ lọc hiện tại</span></div></article>
        <article className="admin-stat"><span className="admin-stat-icon gold"><Clock3 size={18} /></span><div><small>KYC đang chờ</small><strong>{pendingKyc.toLocaleString("vi-VN")}</strong><span>cần được xem xét</span></div></article>
        <article className="admin-stat"><span className="admin-stat-icon red"><LockKeyhole size={18} /></span><div><small>Tài khoản bị khóa</small><strong>{lockedUsers.toLocaleString("vi-VN")}</strong><span>trên toàn hệ thống</span></div></article>
      </div>

      <div className="admin-users-panel">
        <div className="admin-panel-top">
          <div><h2>Danh sách tài khoản</h2><p>Tra cứu và xử lý hồ sơ người dùng</p></div>
          <span className="admin-live"><i /> Dữ liệu hệ thống</span>
        </div>
        <form className="admin-filters" onSubmit={submitSearch}>
          <label className="admin-search"><Search size={16} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Tìm tên hoặc email" /><button aria-label="Tìm kiếm"><ArrowRight size={15} /></button></label>
          <label className="admin-select"><span>Trạng thái</span><select value={status} onChange={(event) => { setStatus(event.target.value as UserStatus | ""); setPage(0); }}><option value="">Tất cả</option><option value="ACTIVE">Hoạt động</option><option value="LOCKED">Đã khóa</option><option value="PENDING">Chờ xử lý</option></select><ChevronDown size={14} /></label>
          <label className="admin-select"><span>Vai trò</span><select value={role} onChange={(event) => { setRole(event.target.value as UserRole | ""); setPage(0); }}><option value="">Tất cả</option><option value="BIDDER">Bidder</option><option value="SELLER">Seller</option><option value="ADMIN">Admin</option></select><ChevronDown size={14} /></label>
          <label className="admin-select"><span>KYC</span><select value={kycStatus} onChange={(event) => { setKycStatus(event.target.value as KycStatus | ""); setPage(0); }}><option value="">Tất cả</option><option value="PENDING">Chờ duyệt</option><option value="APPROVED">Đã xác minh</option><option value="REJECTED">Cần bổ sung</option><option value="NOT_SUBMITTED">Chưa gửi</option></select><ChevronDown size={14} /></label>
          <button className="admin-clear" type="button" onClick={clearFilters}>Xóa lọc</button>
        </form>

        {notice && <div className="admin-notice"><CheckCircle2 size={16} /> {notice}<button onClick={() => setNotice("")} aria-label="Đóng thông báo"><X size={14} /></button></div>}
        {error && <div className="admin-error"><AlertCircle size={16} /> {error}<button onClick={() => setError("")} aria-label="Đóng thông báo"><X size={14} /></button></div>}

        <div className="admin-table-wrap">
          <table className="admin-table">
            <thead><tr><th>Người dùng</th><th>Vai trò</th><th>Tài khoản</th><th>Xác minh KYC</th><th>Ngày tham gia</th><th><span className="sr-only">Thao tác</span></th></tr></thead>
            <tbody>
              {loading ? <tr><td colSpan={6}><div className="admin-empty"><RefreshCw size={20} className="spin" /> Đang tải danh sách…</div></td></tr> : users.length === 0 ? <tr><td colSpan={6}><div className="admin-empty"><UserRound size={22} /><strong>Không tìm thấy tài khoản</strong><span>Thử thay đổi từ khóa hoặc bộ lọc.</span></div></td></tr> : users.map((item) => (
                <tr key={item.id}>
                  <td><button className="admin-user-cell" onClick={() => setSelectedUser(item)}><span className="admin-avatar">{userInitials(item.fullName)}</span><span><strong>{item.fullName}</strong><small>{item.email}</small></span></button></td>
                  <td><span className={`admin-role role-${item.role.toLowerCase()}`}>{roleLabels[item.role]}</span></td>
                  <td><span className={`admin-status status-${item.status.toLowerCase()}`}><i />{statusLabels[item.status]}</span></td>
                  <td><span className={`admin-kyc kyc-${item.kycStatus.toLowerCase()}`}>{item.kycStatus === "APPROVED" ? <CheckCircle2 size={13} /> : item.kycStatus === "PENDING" ? <Clock3 size={13} /> : <FileCheck2 size={13} />}{kycLabels[item.kycStatus]}</span></td>
                  <td className="admin-date">{formatDate(item.createdAt)}</td>
                  <td><div className="admin-row-actions">
                    <button title="Xem hồ sơ" onClick={() => setSelectedUser(item)}><Eye size={15} /></button>
                    {item.kycStatus === "PENDING" && <><button className="action-approve" title="Duyệt KYC" onClick={() => void approveKyc(item)}><Check size={15} /></button><button className="action-reject" title="Từ chối KYC" onClick={() => startAction(item, "reject-kyc")}><X size={15} /></button></>}
                    {item.kycStatus === "APPROVED" && item.role === "BIDDER" && item.status === "ACTIVE" && <button className="action-approve" title="Duyệt Seller" onClick={() => startAction(item, "seller")}><ShieldCheck size={15} /></button>}
                    {item.status === "ACTIVE" ? <button className="action-danger" title="Khóa tài khoản" onClick={() => startAction(item, "lock")}><Ban size={15} /></button> : item.status === "LOCKED" ? <button className="action-approve" title="Mở khóa tài khoản" onClick={() => startAction(item, "unlock")}><UnlockKeyhole size={15} /></button> : null}
                    {item.role !== "ADMIN" && <button title="Cảnh cáo" onClick={() => startAction(item, "warn")}><ShieldAlert size={15} /></button>}
                  </div></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="admin-pagination"><span>Hiển thị {users.length ? page * 10 + 1 : 0}–{Math.min((page + 1) * 10, total)} trong {total.toLocaleString("vi-VN")} tài khoản</span><div><button disabled={page <= 0 || loading} onClick={() => setPage((value) => Math.max(0, value - 1))} aria-label="Trang trước"><ArrowLeft size={15} /></button><span>Trang {totalPages ? page + 1 : 0} / {totalPages}</span><button disabled={page + 1 >= totalPages || loading} onClick={() => setPage((value) => value + 1)} aria-label="Trang sau"><ArrowRight size={15} /></button></div></div>
      </div>

      {selectedUser && !action && <div className="admin-overlay" onMouseDown={(event) => { if (event.target === event.currentTarget) setSelectedUser(null); }}><section className="admin-dialog admin-detail" role="dialog" aria-modal="true" aria-labelledby="detail-title"><button className="admin-dialog-close" onClick={() => setSelectedUser(null)} aria-label="Đóng"><X size={18} /></button><div className="admin-detail-profile"><span className="admin-avatar large">{userInitials(selectedUser.fullName)}</span><div><span className={`admin-role role-${selectedUser.role.toLowerCase()}`}>{roleLabels[selectedUser.role]}</span><h2 id="detail-title">{selectedUser.fullName}</h2><p>{selectedUser.email}</p></div></div><div className="admin-detail-grid"><div><small>Số điện thoại</small><strong>{selectedUser.phone || "Chưa cập nhật"}</strong></div><div><small>Trạng thái tài khoản</small><strong>{statusLabels[selectedUser.status]}</strong></div><div><small>Trạng thái KYC</small><strong>{kycLabels[selectedUser.kycStatus]}</strong></div><div><small>Số giấy tờ</small><strong>{selectedUser.identityNumber || "Chưa gửi"}</strong></div><div><small>Email xác minh</small><strong>{selectedUser.emailVerified ? "Đã xác minh" : "Chưa xác minh"}</strong></div><div><small>Điện thoại xác minh</small><strong>{selectedUser.phoneVerified ? "Đã xác minh" : "Chưa xác minh"}</strong></div></div>{selectedUser.kycRejectionReason && <div className="admin-rejection"><strong>Lý do yêu cầu bổ sung</strong><span>{selectedUser.kycRejectionReason}</span></div>}{selectedUser.identityDocumentUrl && <a className="admin-document-link" href={selectedUser.identityDocumentUrl} target="_blank" rel="noreferrer"><FileCheck2 size={16} /> Mở tài liệu KYC <ArrowRight size={14} /></a>}<div className="admin-detail-foot">Tham gia ngày {formatDate(selectedUser.createdAt)}</div></section></div>}

      {action && selectedUser && <div className="admin-overlay" onMouseDown={(event) => { if (event.target === event.currentTarget && !saving) setAction(null); }}><form className="admin-dialog" onSubmit={(event) => void runAction(event)}><button type="button" className="admin-dialog-close" onClick={() => setAction(null)} aria-label="Đóng" disabled={saving}><X size={18} /></button><span className="admin-dialog-icon"><ShieldAlert size={19} /></span><h2>{modalTitle[action]}</h2><p>Người dùng: <strong>{selectedUser.fullName}</strong></p><label className="admin-reason-label">{action === "reject-kyc" ? "Lý do từ chối / thông tin cần bổ sung" : "Lý do xử lý"}<textarea autoFocus minLength={3} maxLength={500} required value={reason} onChange={(event) => setReason(event.target.value)} placeholder="Nhập lý do cụ thể để lưu vào nhật ký quản trị…" /></label><div className="admin-reason-meta">{reason.length}/500 ký tự · Tối thiểu 3 ký tự</div><div className="admin-dialog-actions"><button type="button" className="admin-secondary" onClick={() => setAction(null)} disabled={saving}>Hủy</button><button className="admin-primary" disabled={saving || reason.trim().length < 3}>{saving ? "Đang lưu…" : "Xác nhận"}</button></div></form></div>}
    </section>
  );
}
