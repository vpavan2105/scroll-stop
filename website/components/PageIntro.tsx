import type { ReactNode } from "react";

/** The heading block at the top of every page except Home. */
export function PageIntro({ label, title, children }: { label: string; title: string; children?: ReactNode }) {
  return (
    <div className="page-intro">
      <div className="wrap">
        <p className="label">{label}</p>
        <h1 style={{ fontSize: "clamp(30px, 5vw, 44px)", margin: "0 0 12px" }}>{title}</h1>
        {children ? <p className="sub" style={{ marginBottom: 0 }}>{children}</p> : null}
      </div>
    </div>
  );
}

/** Shown only while running `npm run dev`, to point at settings that still need filling in. */
export function DevNote({ children }: { children: ReactNode }) {
  if (process.env.NODE_ENV !== "development") return null;
  return <div className="dev-note">Setup: {children}</div>;
}
