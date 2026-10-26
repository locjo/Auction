# AuctionHub Frontend

Frontend đấu giá trực tuyến dùng React, TypeScript strict, Tailwind CSS, Zustand và Vite.

## Chạy ứng dụng

```bash
npm install
npm run dev
```

Mặc định Vite proxy REST và SockJS tới `http://localhost:8080`. Có thể đặt `VITE_API_BASE_URL` và `VITE_WS_URL` trong `.env.local` để đổi địa chỉ backend.

## Lệnh hữu ích

```bash
npm run format
npm run format:check
npm run build
```

## Cấu trúc

```text
frontend/
├── components/   # UI tái sử dụng: cards, form, countdown, notices
├── lib/          # Axios client, format tiền/đếm ngược, xử lý lỗi
├── pages/        # Home, đăng nhập/đăng ký, ví, phòng đấu giá
├── service/      # REST API và STOMP/SockJS
├── stores/       # Zustand: auth, auction, notification
├── types/        # DTO, API response, trạng thái và socket payload
└── src/          # Router, entry point, Tailwind và giao diện nền
```

Access token chỉ nằm trong Zustand memory; refresh token do backend lưu trong HttpOnly cookie. `npm run build` chạy kiểm tra TypeScript strict trước khi đóng gói.
