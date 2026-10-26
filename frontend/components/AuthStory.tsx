import { Gavel, ShieldCheck } from "lucide-react";

const benefits = [
  ["Giao dịch được bảo vệ", "Thanh toán ký quỹ an toàn cho mọi thành viên."],
  ["Đấu giá minh bạch", "Mọi lượt trả giá được ghi nhận theo thời gian thực."],
];

export function AuthStory() {
  return (
    <aside className="auth-story">
      <div className="eyebrow">
        <i /> CỘNG ĐỒNG ĐẤU GIÁ CAO CẤP
      </div>
      <h1>
        Săn tìm điều
        <br />
        xứng đáng <em>sở hữu.</em>
      </h1>
      <p>
        Tham gia cộng đồng nhà sưu tầm và khám phá những món đồ độc bản từ khắp
        nơi.
      </p>
      {benefits.map(([title, description], index) => {
        const Icon = index === 0 ? ShieldCheck : Gavel;
        return (
          <div className="auth-benefit" key={title}>
            <Icon />
            <span>
              <b>{title}</b>
              <small>{description}</small>
            </span>
          </div>
        );
      })}
      <blockquote className="auth-quote">
        “Tôi tìm thấy chiếc đồng hồ mà mình đã săn lùng suốt 5 năm.”
        <small>— Minh Anh, thành viên từ 2022</small>
      </blockquote>
    </aside>
  );
}
