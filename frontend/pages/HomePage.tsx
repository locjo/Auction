import { useEffect, useMemo, useState } from "react";
import { ChevronRight, Search, SlidersHorizontal } from "lucide-react";
import { AuctionCardGrid } from "../components/AuctionCardGrid";
import { Breadcrumb } from "../components/Breadcrumb";
import { FeaturedAuction } from "../components/FeaturedAuction";
import { SectionHeading } from "../components/SectionHeading";
import { SecurityBanner } from "../components/SecurityBanner";
import { TrustHighlights } from "../components/TrustHighlights";
import { getApiErrorMessage } from "../lib/errors";
import { categoryService } from "../service/categoryService";
import { productService } from "../service/productService";
import type { AuctionProduct } from "../types/auction";
import type { CategoryDto } from "../types/category";

interface HomePageProps {
  onSignIn: (productId: number) => void;
}

export function HomePage({ onSignIn }: HomePageProps) {
  const [auctionProducts, setAuctionProducts] = useState<AuctionProduct[]>([]);
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [categoriesLoading, setCategoriesLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [categoriesError, setCategoriesError] = useState<string | null>(null);
  const [category, setCategory] = useState<string | null>(null);
  const [query, setQuery] = useState("");

  useEffect(() => {
    let mounted = true;

    categoryService
      .list()
      .then((results) => {
        if (mounted) setCategories(results);
      })
      .catch((error: unknown) => {
        if (mounted) setCategoriesError(getApiErrorMessage(error));
      })
      .finally(() => {
        if (mounted) setCategoriesLoading(false);
      });

    productService
      .listPublic()
      .then((results) => {
        if (mounted) setAuctionProducts(results);
      })
      .catch((error: unknown) => {
        if (mounted) setLoadError(getApiErrorMessage(error));
      })
      .finally(() => {
        if (mounted) setLoading(false);
      });

    return () => {
      mounted = false;
    };
  }, []);

  const filteredProducts = useMemo(
    () =>
      auctionProducts.filter(
        (product) =>
          (category === null || product.category === category) &&
          product.title.toLowerCase().includes(query.toLowerCase()),
      ),
    [auctionProducts, category, query],
  );
  const featuredProduct =
    auctionProducts.find((product) => product.status === "ACTIVE") ??
    auctionProducts[0];
  const activeProductCount = auctionProducts.filter(
    (product) => product.status === "ACTIVE",
  ).length;

  return (
    <div className="page-wrap">
      <Breadcrumb current="PHIÊN ĐẤU GIÁ" status={`${activeProductCount} phiên đang diễn ra`} />
      {featuredProduct && (
        <FeaturedAuction item={featuredProduct} onBid={onSignIn} />
      )}
      <TrustHighlights />

      <section className="lots-section">
        <SectionHeading
          eyebrow="CƠ HỘI ĐANG MỞ"
          title="Phiên đấu giá"
          emphasis="nổi bật"
          description="Những món đồ độc bản đang chờ chủ nhân tiếp theo."
          action={
            <button className="outline-button">
              Tất cả phiên đấu giá <ChevronRight size={15} />
            </button>
          }
        />

        <div className="filters">
          <div className="category-tabs" aria-label="Lọc theo danh mục">
            <button
              className={category === null ? "selected" : ""}
              aria-pressed={category === null}
              onClick={() => setCategory(null)}
            >
              Tất cả
            </button>
            {categoriesLoading && (
              <span className="muted" role="status">Đang tải danh mục...</span>
            )}
            {categories.map((item) => (
              <button
                key={item.id}
                className={category === item.name ? "selected" : ""}
                aria-pressed={category === item.name}
                onClick={() => setCategory(item.name)}
              >
                {item.name}
              </button>
            ))}
          </div>
          <div className="filter-tools">
            <label className="search-field">
              <Search size={15} />
              <input
                placeholder="Tìm sản phẩm..."
                value={query}
                onChange={(event) => setQuery(event.target.value)}
              />
            </label>
            <button className="filter-button">
              <SlidersHorizontal size={15} /> Bộ lọc
            </button>
          </div>
        </div>

        {categoriesError && (
          <div className="notice" role="status">
            Không tải được danh mục từ máy chủ. {categoriesError}
          </div>
        )}
        {loadError && (
          <div className="notice" role="status">
            Không kết nối được máy chủ nên chưa thể tải phiên đấu giá. {loadError}
          </div>
        )}
        <AuctionCardGrid
          products={filteredProducts}
          loading={loading}
          onBid={onSignIn}
        />
      </section>

      <SecurityBanner />
    </div>
  );
}
