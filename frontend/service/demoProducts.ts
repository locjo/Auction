import type { AuctionProduct } from "../types/auction";

const featuredAuctionEnd = new Date(
  Date.now() + 2 * 60 * 60 * 1_000 + 14 * 60 * 1_000 + 55 * 1_000,
).toISOString();

export const products: AuctionProduct[] = [
  {
    id: 1,
    title: "Porsche 911 Carrera S · 2021",
    category: "Ô tô",
    price: 3280000000,
    bids: 24,
    time: "02:14:55",
    endTime: featuredAuctionEnd,
    image:
      "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: 2,
    title: "Rolex Submariner Date 126610LN",
    category: "Đồng hồ",
    price: 385000000,
    bids: 18,
    time: "04:32:10",
    image:
      "https://images.unsplash.com/photo-1523170335258-f5ed11844a49?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: 3,
    title: "Omega Speedmaster Moonwatch",
    category: "Đồng hồ",
    price: 142000000,
    bids: 12,
    time: "06:08:42",
    image:
      "https://images.unsplash.com/photo-1524592094714-0f0654e20314?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: 4,
    title: "Tranh sơn dầu phố cổ Hà Nội",
    category: "Nghệ thuật",
    price: 28700000,
    bids: 9,
    time: "01:56:20",
    image:
      "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: 5,
    title: "Leica M6 TTL · Black Chrome",
    category: "Sưu tầm",
    price: 76500000,
    bids: 15,
    time: "08:22:05",
    image:
      "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: 6,
    title: "MacBook Pro 16” M3 Max · 2024",
    category: "Đồ công nghệ",
    price: 52300000,
    bids: 7,
    time: "12:44:31",
    image:
      "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: 7,
    title: "Mercedes-Benz 280 SL Pagoda",
    category: "Ô tô",
    price: 2150000000,
    bids: 31,
    time: "1 ngày 02 giờ",
    image:
      "https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: 8,
    title: "Túi Hermès Kelly 28 Togo",
    category: "Sưu tầm",
    price: 198000000,
    bids: 11,
    time: "1 ngày 06 giờ",
    image:
      "https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=900&q=85",
  },
  {
    id: 9,
    title: "Cartier Santos de Cartier Large",
    category: "Đồng hồ",
    price: 134000000,
    bids: 16,
    time: "1 ngày 12 giờ",
    image:
      "https://images.unsplash.com/photo-1547996160-81dfa63595aa?auto=format&fit=crop&w=900&q=85",
  },
];
