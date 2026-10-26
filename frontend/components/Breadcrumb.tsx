interface BreadcrumbProps {
  current: string;
  status?: string;
}

export function Breadcrumb({ current, status }: BreadcrumbProps) {
  return (
    <div className="breadcrumb">
      <span>TRANG CHỦ</span>
      <b>/</b>
      <span>{current}</span>
      {status && (
        <span className="crumb-right">
          <i /> {status}
        </span>
      )}
    </div>
  );
}
