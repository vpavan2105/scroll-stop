import type { Metadata } from "next";
import { CopyButton } from "@/components/CopyButton";
import { Icon } from "@/components/Icon";
import { DevNote, PageIntro } from "@/components/PageIntro";
import { upiLink, upiQrSvg } from "@/lib/upi";
import { site } from "@/site.config";

export const metadata: Metadata = { title: "Buy me a tea" };

export default async function Support() {
  const { upiId, amounts, internationalUrl, internationalLabel } = site.support;
  const qr = upiId ? await upiQrSvg() : null;

  return (
    <>
      <PageIntro label="Buy me a tea" title="ScrollStop is free, and stays free">
        No ads, no subscriptions, no paid features. If it gave you some time back and you&apos;d like to say thanks, a tea keeps the project going.
      </PageIntro>
      <div className="section">
        <div className="wrap">
          <div className="grid g2">
            {upiId && qr ? (
              <>
                <div className="card">
                  <div className="tile amber"><Icon name="tea" size={22} /></div>
                  <h3>Pay with UPI</h3>
                  <p>On your phone, tap an amount. Your UPI app (GPay, PhonePe, Paytm…) opens with it filled in.</p>
                  <div className="amounts" style={{ marginTop: 16 }}>
                    {amounts.map((a) => (
                      <a key={a} className="btn btn-primary btn-small" href={upiLink(a)}>₹{a}</a>
                    ))}
                    <a className="btn btn-secondary btn-small" href={upiLink()}>Other amount</a>
                  </div>
                  <div className="copy-row" style={{ marginTop: 18 }}>
                    <code>{upiId}</code>
                    <CopyButton value={upiId} label="Copy UPI ID" />
                  </div>
                </div>
                <div className="card" style={{ display: "flex", flexDirection: "column", alignItems: "center", textAlign: "center" }}>
                  <h3>On a computer? Scan this</h3>
                  <p style={{ marginBottom: 16 }}>Open any UPI app on your phone and scan.</p>
                  <div className="qr" role="img" aria-label={`UPI QR code for ${upiId}`} dangerouslySetInnerHTML={{ __html: qr }} />
                </div>
              </>
            ) : (
              <div className="card">
                <div className="tile amber"><Icon name="tea" size={22} /></div>
                <h3>Thank you</h3>
                <p>Telling a friend who scrolls too much is the best support there is.</p>
                <DevNote>set support.upiId in site.config.ts to show UPI pay buttons and a QR code.</DevNote>
              </div>
            )}
          </div>

          {internationalUrl ? (
            <div className="note" style={{ marginTop: 16 }}>
              <Icon name="info" color="#A3A8B4" />
              <span>Outside India? <a href={internationalUrl}>{internationalLabel}</a>.</span>
            </div>
          ) : null}

          <p className="fine">Support is a thank-you, not a purchase: everyone gets exactly the same app, and nothing is unlocked.</p>
        </div>
      </div>
    </>
  );
}
