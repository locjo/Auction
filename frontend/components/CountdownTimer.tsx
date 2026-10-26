import { useEffect, useState } from "react";
import { formatCountdown } from "../lib/format";

interface CountdownTimerProps {
  endTime: string | null;
  fallback?: string;
}

export function CountdownTimer({
  endTime,
  fallback = "02:14:55",
}: CountdownTimerProps) {
  const [now, setNow] = useState(Date.now());

  useEffect(() => {
    const timerId = window.setInterval(() => setNow(Date.now()), 1_000);
    return () => window.clearInterval(timerId);
  }, []);

  return <b>{endTime ? formatCountdown(endTime, now) : fallback}</b>;
}
