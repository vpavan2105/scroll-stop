/** The same line icons as the app (24×24, 2 px stroke). Colour comes from `currentColor`. */
const paths = {
  shield: ["M12 3l7 3v5c0 4.5-3 8.5-7 10-4-1.5-7-5.5-7-10V6l7-3z", "M9 12l2 2 4-4"],
  reelsOff: ["M8 3h8a5 5 0 015 5v8a5 5 0 01-5 5H8a5 5 0 01-5-5V8a5 5 0 015-5z", "M10 8.5l5 3.5-5 3.5z", "M4 4l16 16"],
  following: ["M5.5 8a3.5 3.5 0 107 0 3.5 3.5 0 10-7 0", "M2.5 20c.8-3.5 3.4-5.5 6.5-5.5s5.7 2 6.5 5.5", "M16 4.5a3.5 3.5 0 010 7", "M18 14.8c1.9.7 3.1 2.5 3.5 5.2"],
  clock: ["M3 12a9 9 0 1018 0 9 9 0 10-18 0", "M12 7v5l3 2"],
  lock: ["M7 11h10a2 2 0 012 2v6a2 2 0 01-2 2H7a2 2 0 01-2-2v-6a2 2 0 012-2z", "M8 11V8a4 4 0 018 0v3"],
  wrench: ["M14.7 6.3a4 4 0 00-5.4 5.4L4 17l3 3 5.3-5.3a4 4 0 005.4-5.4l-2.5 2.5-2.5-.5-.5-2.5z"],
  download: ["M12 4v11", "M7 10l5 5 5-5", "M5 20h14"],
  info: ["M3 12a9 9 0 1018 0 9 9 0 10-18 0", "M12 11v5", "M12 8v.5"],
  check: ["M5 12.5l4.5 4.5L19 7.5"],
  arrow: ["M5 12h14", "M13 6l6 6-6 6"],
  menu: ["M4 7h16", "M4 12h16", "M4 17h16"],
  close: ["M6 6l12 12", "M18 6L6 18"],
  mail: ["M4 6h16v12H4z", "M4 7l8 6 8-6"],
  tea: ["M5 9h11v5a5 5 0 01-5 5h-1a5 5 0 01-5-5V9z", "M16 10h1.5a2.5 2.5 0 010 5H16", "M8 3.5c0 1 1 1.5 1 2.5", "M12 3.5c0 1 1 1.5 1 2.5"],
  eye: ["M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z", "M9 12a3 3 0 106 0 3 3 0 10-6 0"],
  wifiOff: ["M2 8.5a15 15 0 0120 0", "M5.5 12a10 10 0 0113 0", "M9 15.5a5 5 0 016 0", "M3 3l18 18"],
  copy: ["M9 9h11v11H9z", "M5 15H4V4h11v1"],
  code: ["M8.5 9L5 12l3.5 3", "M15.5 9L19 12l-3.5 3", "M13.5 6.5l-3 11"],
} as const;

export type IconName = keyof typeof paths;

export function Icon({ name, size = 20, strokeWidth = 2, color }: { name: IconName; size?: number; strokeWidth?: number; color?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color ?? "currentColor"} strokeWidth={strokeWidth} strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      {paths[name].map((d) => (
        <path key={d} d={d} />
      ))}
    </svg>
  );
}

export function Logo({ size = 32 }: { size?: number }) {
  return (
    <svg width={size} height={size} viewBox="0 0 108 108" aria-hidden="true">
      <rect width="108" height="108" rx="26" fill="#1F222A" />
      <path d="M45 39 69 54 45 69Z" fill="#F5B13D" />
      <path d="M36 36 72 72" stroke="#F5B13D" strokeWidth="6" strokeLinecap="round" />
    </svg>
  );
}
