import type { ReactNode } from "react";

interface SectionHeadingProps {
  eyebrow: string;
  title: string;
  emphasis: string;
  description: string;
  action?: ReactNode;
  className?: string;
}

export function SectionHeading({
  eyebrow,
  title,
  emphasis,
  description,
  action,
  className = "",
}: SectionHeadingProps) {
  return (
    <div className={`section-heading ${className}`.trim()}>
      <div>
        <div className="eyebrow section-eyebrow">
          <i /> {eyebrow}
        </div>
        <h2>
          {title} <em>{emphasis}</em>
        </h2>
        <p>{description}</p>
      </div>
      {action}
    </div>
  );
}
