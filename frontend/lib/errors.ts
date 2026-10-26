import axios from "axios";
import type { ApiErrorBody } from "../types/api";

export function getApiErrorMessage(error: unknown): string {
  if (!axios.isAxiosError<ApiErrorBody>(error)) {
    return "Đã có lỗi xảy ra. Vui lòng thử lại.";
  }

  const status = error.response?.status;
  const body = error.response?.data;
  const code = body?.errorCode;

  if (status === 409 || code === "BID_CONFLICT" || code === "CONFLICT") {
    return "Giá vừa được cập nhật bởi người khác. Hãy kiểm tra giá mới rồi thử lại.";
  }
  if (status === 401)
    return "Thông tin đăng nhập không chính xác hoặc phiên đã hết hạn.";
  if (status === 0 || !error.response)
    return "Không thể kết nối máy chủ. Vui lòng thử lại.";
  return body?.message ?? "Yêu cầu chưa thực hiện được. Vui lòng thử lại.";
}
