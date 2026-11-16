import type { Metadata } from "next";
import { CopyButton } from "@/components/CopyButton";
import { Icon } from "@/components/Icon";
import { DevNote, PageIntro } from "@/components/PageIntro";
import { upiLink, upiQrSvg } from "@/lib/upi";
import { site } from "@/site.config";

export const metadata: Metadata = { title: "Buy me a coffee" };

export default async function Support() {
  const { upiId, amounts, internationalUrl } = site.support;
  const qr = upiId ? await upiQrSvg() : null;

  return (
    <>
      <PageIntro label="Buy me a coffee" title="ScrollStop is free, and stays free">
        No ads, no subscriptions, no paid features. If it gave you some time back and you&apos;d like to say thanks, a coffee keeps the project going.
      </PageIntro>
      <div className="section">
        <div className="wrap">
          <div className="grid g2">
            {/* Buy Me a Coffee — for everyone */}
            {internationalUrl ? (
              <a className="card bmc-card" href={internationalUrl} target="_blank" rel="noopener noreferrer">
                <div className="tile amber"><Icon name="tea" size={22} /></div>
                <h3>Buy me a coffee</h3>
                <p>Card, Apple Pay or Google Pay, from anywhere. Opens my page in a new tab.</p>
                <span className="btn btn-primary btn-small" style={{ marginTop: 16 }}>
                  <Icon name="tea" size={16} /> Buy me a coffee
                </span>
              </a>
            ) : (
              <div className="card">
                <div className="tile amber"><Icon name="tea" size={22} /></div>
                <h3>Thank you</h3>
                <p>Telling a friend who scrolls too much is the best support there is.</p>
                <DevNote>set support.internationalUrl in site.config.ts to show a Buy Me a Coffee button.</DevNote>
              </div>
            )}

            {/* UPI — convenient inside India (shown only when set) */}
            {upiId ? (
              <div className="card">
                <div className="tile amber"><Icon name="tea" size={22} /></div>
                <h3>In India? Pay with UPI</h3>
                <p>Tap an amount on your phone. GPay, PhonePe or Paytm opens with it filled in.</p>
                <div className="amounts" style={{ marginTop: 16 }}>
                  {amounts.map((a) => (
                    <a key={a} className="btn btn-primary btn-small" href={upiLink(a)}>₹{a}</a>
                  ))}
                  <a className="btn btn-secondary btn-small" href={upiLink()}>Other</a>
                </div>
                <div className="copy-row" style={{ marginTop: 18 }}>
                  <code>{upiId}</code>
                  <CopyButton value={upiId} label="Copy UPI ID" />
                </div>
              </div>
            ) : (
              <div className="card">
                <div className="tile"><Icon name="following" size={22} /></div>
                <h3>Or just spread the word</h3>
                <p>Share ScrollStop with a friend who opens Instagram for one message and looks up 40 minutes later. That helps just as much.</p>
              </div>
            )}
          </div>

          {upiId && qr ? (
            <div className="card" style={{ marginTop: 16, display: "flex", flexDirection: "column", alignItems: "center", textAlign: "center" }}>
              <h3>On a computer? Scan to pay by UPI</h3>
              <p style={{ marginBottom: 16 }}>Open any UPI app on your phone and scan.</p>
              <div className="qr" role="img" aria-label={`UPI QR code for ${upiId}`} dangerouslySetInnerHTML={{ __html: qr }} />
            </div>
          ) : null}

          <p className="fine">Support is a thank-you, not a purchase: everyone gets exactly the same app, and nothing is unlocked.</p>
        </div>
      </div>
    </>
  );
}
