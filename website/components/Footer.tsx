import Link from "next/link";
import { latest } from "@/site.config";

export function Footer() {
  return (
    <footer className="foot">
      <div className="wrap">
        <span>ScrollStop {latest.version} · free, no ads, no internet access</span>
        <nav aria-label="Footer">
          <Link href="/why">Why not Play Store</Link>
          <Link href="/privacy">Privacy</Link>
          <Link href="/contact">Contact</Link>
          <Link href="/support">Buy me a tea</Link>
          <a href="https://vpktech.online" target="_blank" rel="noopener">More of my work</a>
        </nav>
      </div>
    </footer>
  );
}
