"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useEffect, useState } from "react";
import { Icon, Logo } from "./Icon";

const links = [
  { href: "/features", label: "Features" },
  { href: "/how-it-works", label: "How it works" },
  { href: "/install", label: "Install" },
  { href: "/versions", label: "Versions" },
  { href: "/faq", label: "FAQ" },
  { href: "/support", label: "Buy me a tea" },
];

/** Top bar. The Download button gets the latest APK straight away (no extra page). */
export function Header({ downloadHref }: { downloadHref: string }) {
  const pathname = usePathname();
  const [open, setOpen] = useState(false);
  useEffect(() => setOpen(false), [pathname]); // close the phone menu after navigating

  const nav = links.map((l) => (
    <Link key={l.href} href={l.href} aria-current={pathname === l.href ? "page" : undefined}>
      {l.label}
    </Link>
  ));

  return (
    <header className="top">
      <div className="wrap">
        <Link className="brand" href="/" aria-label="ScrollStop home">
          <Logo />
          ScrollStop
        </Link>
        <nav className="nav" aria-label="Main">{nav}</nav>
        <a className="btn btn-primary btn-small header-cta" href={downloadHref} download>
          <Icon name="download" size={18} strokeWidth={2.2} />
          Download
        </a>
        <button
          className="menu-btn"
          type="button"
          aria-expanded={open}
          aria-controls="mobile-nav"
          aria-label={open ? "Close menu" : "Open menu"}
          onClick={() => setOpen((o) => !o)}
        >
          <Icon name={open ? "close" : "menu"} />
        </button>
      </div>
      <nav id="mobile-nav" className={`mobile-nav${open ? " open" : ""}`} aria-label="Main">
        <a className="btn btn-primary btn-small" href={downloadHref} download>
          <Icon name="download" size={18} strokeWidth={2.2} />
          Download latest
        </a>
        {nav}
      </nav>
    </header>
  );
}
