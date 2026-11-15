import type { Metadata } from "next";
import { CopyButton } from "@/components/CopyButton";
import { Icon } from "@/components/Icon";
import { DevNote, PageIntro } from "@/components/PageIntro";
import { latest, site } from "@/site.config";

export const metadata: Metadata = { title: "Contact" };

export default function Contact() {
  const email = site.contactEmail;
  const subject = `ScrollStop ${latest.version} question`;
  // Opens a new message in Gmail on the web (or the Gmail app on many phones).
  const gmail = `https://mail.google.com/mail/?view=cm&fs=1&to=${encodeURIComponent(email)}&su=${encodeURIComponent(subject)}`;
  const mailto = `mailto:${email}?subject=${encodeURIComponent(subject)}`;

  return (
    <>
      <PageIntro label="Contact" title="Questions or problems?">
        Send an email through Gmail. Replies may take a few days: this is a personal project.
      </PageIntro>
      <div className="section">
        <div className="wrap" style={{ maxWidth: 820 }}>
          {email ? (
            <div className="card">
              <div className="tile amber"><Icon name="mail" size={22} /></div>
              <h3>Email</h3>
              <div className="copy-row">
                <code>{email}</code>
                <CopyButton value={email} label="Copy address" />
              </div>
              <div className="btn-row" style={{ marginTop: 18 }}>
                <a className="btn btn-primary" href={gmail}>Write in Gmail</a>
                <a className="btn btn-secondary" href={mailto}>Use my email app</a>
              </div>
            </div>
          ) : (
            <DevNote>set contactEmail in site.config.ts. Until then this page shows only the tips below.</DevNote>
          )}

          <div className="card" style={{ marginTop: 16 }}>
            <h3>To get help faster, include</h3>
            <ul className="check" style={{ marginTop: 12 }}>
              {[
                "Your phone brand and model, and Android version",
                "The ScrollStop version (Settings › Apps › ScrollStop)",
                "Your Instagram or YouTube version (Settings › Apps)",
                "What you expected, and what happened instead",
                "If a tab came back: a capture from ScrollStop › After an app update › Capture screen",
              ].map((t) => (
                <li key={t}><Icon name="check" size={16} strokeWidth={3} color="#5BD6A5" />{t}</li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </>
  );
}
