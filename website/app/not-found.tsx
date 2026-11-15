import Link from "next/link";

export default function NotFound() {
  return (
    <div className="page-intro">
      <div className="wrap">
        <p className="label">404</p>
        <h1 style={{ fontSize: "clamp(30px, 5vw, 44px)" }}>This page doesn&apos;t exist</h1>
        <p className="sub">It may have moved. Everything about ScrollStop starts on the home page.</p>
        <Link className="btn btn-primary" href="/">Go home</Link>
      </div>
    </div>
  );
}
