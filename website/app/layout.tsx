import type { Metadata, Viewport } from "next";
import localFont from "next/font/local";
import type { ReactNode } from "react";
import { Footer } from "@/components/Footer";
import { Header } from "@/components/Header";
import { latest, site } from "@/site.config";
import "./globals.css";

// The same fonts as the app, served from this site (no Google request).
const body = localFont({
  src: [
    { path: "../fonts/Inter-Regular.ttf", weight: "400" },
    { path: "../fonts/Inter-Medium.ttf", weight: "500" },
    { path: "../fonts/Inter-SemiBold.ttf", weight: "600" },
    { path: "../fonts/Inter-Bold.ttf", weight: "700" },
  ],
  variable: "--font-body",
  display: "swap",
});
const display = localFont({
  src: [
    { path: "../fonts/SpaceGrotesk-SemiBold.ttf", weight: "600" },
    { path: "../fonts/SpaceGrotesk-Bold.ttf", weight: "700" },
  ],
  variable: "--font-display",
  display: "swap",
});

export const metadata: Metadata = {
  metadataBase: new URL(site.url),
  title: { default: `${site.name}: ${site.tagline}`, template: `%s · ${site.name}` },
  description: site.description,
  openGraph: { title: site.name, description: site.description, type: "website" },
};

export const viewport: Viewport = { themeColor: "#0E0F13" };

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en" className={`${body.variable} ${display.variable}`}>
      <body>
        <a className="skip" href="#main">Skip to content</a>
        <Header downloadHref={`/${latest.file}`} />
        <main id="main">{children}</main>
        <Footer />
      </body>
    </html>
  );
}
