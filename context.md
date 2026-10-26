# Project Context & Rules: Online Auction System

## 1. Tech Stack (Công nghệ cốt lõi)
- **Language & Framework:** Java 21, Spring Boot 3.5.3
- **Database & ORM:** PostgreSQL / MySQL, Spring Data JPA, Hibernate
- **Security:** Spring Security, JWT (JSON Web Token), OAuth2 Client
- **Real-time Bidding:** Spring WebSocket (STOMP protocol)
- **Caching & Concurrency:** Spring Data Redis (Cache & Distributed Lock)
- **Payment Gateway:** VNPay Sandbox (Tích hợp mã hóa HMAC-SHA512, IPN & Return URL)
- **Build Tool:** Maven Wrapper 3.10.0

Phiên bản backend được ghim trong `backend/pom.xml`: Java/release 21, Spring Boot 3.5.3, Lombok 1.18.48 và JJWT 0.12.6. Runtime Docker dùng Eclipse Temurin 21; PostgreSQL 17 và Redis 7 dùng image riêng tương ứng.

## 2. Architecture & Package Structure (Kiến trúc phân lớp & Module)
Dự án áp dụng mô hình **Layered Architecture (N-tier)** kết hợp phân chia theo module nghiệp vụ:

```text
com.auction
├── config                # Cấu hình chung (Security, WebSocket, Redis, VnpayConfig, OpenApi)
├── exception             # Global Exception Handler (@ControllerAdvice) & Custom Exceptions
├── security              # JWT Token Provider, Filter, UserDetailsService
├── common                # ApiResponse wrapper, BaseEntity
└── modules               # Các module nghiệp vụ chính
    ├── auth              # Đăng ký, Đăng nhập, JWT
    ├── user              # Quản lý thông tin cá nhân
    ├── wallet            # Quản lý số dư ví & tiền cọc
    ├── category          # Danh mục sản phẩm
    ├── product           # Sản phẩm đấu giá (Có @Version cho Optimistic Locking)
    ├── bid               # Đặt giá thời gian thực (WebSocket & Concurrency check)
    ├── order             # Đơn hàng khi chốt phiên đấu giá
    └── payment           # Xử lý thanh toán VNPay (VnpayService, IPN callback)
3. Coding Standards & Conventions (Quy chuẩn code)
Lombok: Sử dụng @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder để tối ưu boilerplate code.

Dependency Injection: Dùng Constructor Injection (khai báo private final, tuyệt đối không dùng @Autowired trên field).

Validation: Bắt buộc validate dữ liệu đầu vào tại DTO (@NotNull, @NotBlank, @Min, @Positive).

Exception Handling: Không throw RuntimeException chung chung. Phải dùng Custom Exception (ResourceNotFoundException, BadRequestException, InsufficientBalanceException) và bắt lại tập trung tại GlobalExceptionHandler.

4. Business Logic Rules (Quy tắc nghiệp vụ cốt lõi)
Kiểm tra giá thầu (Bid Validation): Giá đặt mới (bid_amount) bắt buộc phải lớn hơn hoặc bằng current_price + step_price.

Chống tranh chấp đồng thời (Concurrency Control): Khi nhiều người dùng cùng bấm Bid vào giây cuối, phải sử dụng cơ chế Optimistic Locking (@Version trên Entity Product) hoặc Redis Lock để tránh Race Condition (lệch giá / ghi đè dữ liệu).

Trạng thái phiên đấu giá: PENDING (chờ diễn ra) -> ACTIVE (đang diễn ra) -> CLOSED (đã kết thúc, tự động chốt đơn cho người chiến thắng).

Luồng thanh toán VNPay & Ví:

Người dùng nạp tiền vào ví (wallet) thông qua cổng VNPay (DEPOSIT).

Sử dụng số dư khả dụng trong ví hoặc tiền cọc để tham gia đấu giá và thanh toán đơn hàng khi thắng cuộc.

Xử lý chuẩn xác cơ chế IPN (Instant Payment Notification) từ VNPay để cập nhật trạng thái giao dịch an toàn, chống gian lận.

# DATABASE SCHEMA CONTEXT: ONLINE AUCTION SYSTEM (VNPay + Wallet)

## 5. Sơ đồ quan hệ các bảng (Entity Relationship / DBML)
```text
categories.id < products.categoryId
users.id < products.sellerId
products.id < bids.productId
users.id < bids.userId
users.id - wallets.userId
users.id < transactions.userId
orders.id - products.id
users.id < orders.winnerId
users.id < orders.sellerId
orders.id < transactions.orderId

6. Chi tiết cấu trúc các bảng (Tables)
A. Bảng users (Quản lý người dùng)
id (BIGINT, PK, Auto Increment): ID người dùng

email (VARCHAR(255), UNIQUE, NOT NULL): Email đăng nhập

password (VARCHAR(255), NOT NULL): Mật khẩu đã mã hóa (BCrypt)

full_name (VARCHAR(100), NOT NULL): Họ và tên

phone (VARCHAR(20)): Số điện thoại

role (VARCHAR(50), NOT NULL): Vai trò (ADMIN, SELLER, BIDDER)

status (VARCHAR(50), NOT NULL): Trạng thái (ACTIVE, LOCKED, PENDING)

created_at (TIMESTAMP): Thời gian tạo

B. Bảng wallets (Quản lý số dư ví & tiền cọc)
id (BIGINT, PK, Auto Increment): ID ví

user_id (BIGINT, FK -> users.id, UNIQUE, NOT NULL): Chủ sở hữu ví

balance (DECIMAL(15,2), NOT NULL, DEFAULT 0): Số dư khả dụng

locked_balance (DECIMAL(15,2), NOT NULL, DEFAULT 0): Tiền đang bị phong tỏa (đặt cọc đấu giá)

C. Bảng categories (Danh mục sản phẩm)
id (BIGINT, PK, Auto Increment): ID danh mục

name (VARCHAR(100), NOT NULL, UNIQUE): Tên danh mục (Laptop, Điện thoại, Đồng hồ...)

slug (VARCHAR(100), NOT NULL): Đường dẫn thân thiện URL

D. Bảng products (Sản phẩm đấu giá — Cốt lõi)
id (BIGINT, PK, Auto Increment): ID sản phẩm

seller_id (BIGINT, FK -> users.id, NOT NULL): Người đăng bán

category_id (BIGINT, FK -> categories.id, NOT NULL): Thuộc danh mục nào

title (VARCHAR(255), NOT NULL): Tên sản phẩm đấu giá

description (TEXT): Mô tả chi tiết

initial_price (DECIMAL(15,2), NOT NULL): Giá khởi điểm

current_price (DECIMAL(15,2), NOT NULL): Giá hiện tại (cập nhật theo bid cao nhất)


start_time (TIMESTAMP, NOT NULL): Thời điểm bắt đầu phiên

end_time (TIMESTAMP, NOT NULL): Thời điểm kết thúc phiên

status (VARCHAR(50), NOT NULL): Trạng thái (PENDING, ACTIVE, SOLD)

winner_id (BIGINT, FK -> users.id): Người thắng cuộc khi phiên kết thúc

version (BIGINT, NOT NULL, DEFAULT 0): Trường quan trọng dùng cho Optimistic Locking (@Version) chống tranh chấp đồng thời khi bid.

created_at (TIMESTAMP)

E. Bảng bids (Lịch sử đặt giá / Thầu)
id (BIGINT, PK, Auto Increment): ID lượt bid

product_id (BIGINT, FK -> products.id, NOT NULL): Sản phẩm được đặt giá

user_id (BIGINT, FK -> users.id, NOT NULL): Người đặt giá

bid_amount (DECIMAL(15,2), NOT NULL): Mức giá đưa ra

created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Thời điểm đặt giá

F. Bảng orders (Đơn hàng sau khi chốt phiên)
id (BIGINT, PK, Auto Increment): ID đơn hàng

product_id (BIGINT, FK -> products.id, UNIQUE, NOT NULL): Sản phẩm thắng đấu giá

winner_id (BIGINT, FK -> users.id, NOT NULL): Người chiến thắng

seller_id (BIGINT, FK -> users.id, NOT NULL): Người bán

final_price (DECIMAL(15,2), NOT NULL): Giá chốt cuối cùng

shipping_address (TEXT, NOT NULL): Địa chỉ nhận hàng

status (VARCHAR(50), NOT NULL): Trạng thái (PENDING_PAYMENT, PAID, SHIPPING, COMPLETED, CANCELLED)

created_at (TIMESTAMP)

G. Bảng transactions (Lịch sử nạp tiền qua VNPay & Thanh toán)
id (BIGINT, PK, Auto Increment): ID giao dịch

user_id (BIGINT, FK -> users.id, NOT NULL): Người thực hiện giao dịch

order_id (BIGINT, FK -> orders.id): Liên kết đơn hàng (nếu là thanh toán đơn hàng)

amount (DECIMAL(15,2), NOT NULL): Số tiền giao dịch

type (VARCHAR(50), NOT NULL): Loại giao dịch (DEPOSIT - Nạp tiền ví, PAYMENT - Thanh toán đơn, BID_DEPOSIT - Đặt cọc)

status (VARCHAR(50), NOT NULL): Trạng thái (PENDING, SUCCESS, FAILED)

vnp_transaction_no (VARCHAR(255)): Mã giao dịch do VNPay trả về (dùng để đối soát qua IPN)

created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
