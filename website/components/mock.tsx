/** Shared drawn-UI primitives: bottom-nav icons and mock Android settings screens. */
import type { ReactNode } from "react";

export function NavIcon({ name, covered }: { name: "home" | "search" | "reels" | "plus" | "user"; covered?: boolean }) {
  const paths: Record<string, ReactNode> = {
    home: <path d="M3 10.5 12 3l9 7.5V21H3z" />,
    search: <><circle cx="11" cy="11" r="7" /><path d="m21 21-4.3-4.3" /></>,
    reels: <><rect x="3" y="3" width="18" height="18" rx="4" /><path d="M10 9l5 3-5 3z" /></>,
    plus: <><rect x="3" y="3" width="18" height="18" rx="5" /><path d="M12 8v8M8 12h8" /></>,
    user: <><circle cx="12" cy="8" r="3.5" /><path d="M5 21c0-3.9 3.1-7 7-7s7 3.1 7 7" /></>,
  };
  return (
    <span className="scn-ni">
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"
        strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">{paths[name]}</svg>
      {covered && <i className="scn-cover" aria-hidden="true" />}
    </span>
  );
}

export function Toggle({ on }: { on?: boolean }) {
  return <span className={`sw${on ? " on" : ""}`} aria-hidden="true"><i /></span>;
}

export type SRow = { label: string; sub?: string; toggle?: boolean; hl?: boolean; chevron?: boolean };

/** A small mock of an Android Settings page, with a highlighted row for the control to tap. */
export function AndroidSetting({ title, rows }: { title: string; rows: SRow[] }) {
  return (
    <div className="mock" role="img" aria-label={`${title} settings, with the ScrollStop control highlighted`}>
      <div className="mock-sc">
        <div className="mock-bar">{title}</div>
        {rows.map((r, i) => (
          <div className={`mock-row${r.hl ? " hl" : ""}`} key={i}>
            <span className="ml"><b>{r.label}</b>{r.sub ? <span>{r.sub}</span> : null}</span>
            {r.toggle !== undefined ? <Toggle on={r.toggle} /> : r.chevron ? <span className="chev" aria-hidden="true">›</span> : null}
          </div>
        ))}
      </div>
    </div>
  );
}
