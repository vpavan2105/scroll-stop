/** Drawn illustrations of the states you'll actually see. Sample numbers. */
import type { ReactNode } from "react";
import { NavIcon } from "@/components/mock";

function Phone({ children, label }: { children: ReactNode; label: string }) {
  return (
    <div className="scn-phone" role="img" aria-label={label}>
      <div className="scn-screen">{children}</div>
    </div>
  );
}

export function ScenarioPhones() {
  return (
    <div className="scn-grid">
      {/* 1. Under the limit */}
      <div className="scn">
        <Phone label="Instagram under the daily limit: all tabs usable, ScrollStop shows 12 of 30 reels today.">
          <div className="scn-head"><span>Instagram</span><span className="scn-pill mint">Reels 12/30</span></div>
          <div className="scn-body">
            <div className="scn-line" style={{ width: "60%" }} />
            <div className="scn-block" />
          </div>
          <div className="scn-nav">
            <NavIcon name="home" /><NavIcon name="search" /><NavIcon name="reels" /><NavIcon name="plus" /><NavIcon name="user" />
          </div>
        </Phone>
        <p className="scn-cap"><b>Under your limit.</b> Every tab works, so you can still search and look people up.</p>
      </div>

      {/* 2. Limit reached — Instagram Home feed covered */}
      <div className="scn">
        <Phone label="Instagram Home after the daily limit: the suggested feed is covered with a 'daily limit reached' panel and a Back to Following button, and the Explore and Reels tabs are covered.">
          <div className="scn-head"><span>Instagram</span><span className="scn-pill">Limit reached</span></div>
          <div className="scn-body">
            <div className="scn-line" style={{ width: "55%" }} />
            <div className="scn-coverpanel">
              <span className="cp-ico" aria-hidden="true">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="9" /><path d="M12 7v5l3 2" /></svg>
              </span>
              <b>That&apos;s today&apos;s limit</b>
              <span className="cp-chip">Back to Following</span>
            </div>
          </div>
          <div className="scn-nav">
            <NavIcon name="home" /><NavIcon name="search" covered /><NavIcon name="reels" covered /><NavIcon name="plus" /><NavIcon name="user" />
          </div>
        </Phone>
        <p className="scn-cap"><b>Limit used up.</b> The Home feed is covered and sends you back to Following; the Reels and Explore tabs hide too.</p>
      </div>

      {/* 3. Switched off for a restricted app */}
      <div className="scn">
        <Phone label="ScrollStop switched off for a restricted app: a full-screen reminder covers Instagram with a button to turn ScrollStop back on.">
          <div className="scn-head"><span>Instagram</span><span className="scn-pill">Switched off</span></div>
          <div className="scn-off">
            <span className="scn-off-ico" aria-hidden="true">
              <svg width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"
                strokeLinecap="round" strokeLinejoin="round"><rect x="4" y="10" width="16" height="10" rx="2" /><path d="M8 10V7a4 4 0 0 1 8 0v3" /></svg>
            </span>
            <h4>ScrollStop is off</h4>
            <p>Instagram is paused while it&apos;s switched off. Turn ScrollStop back on when you&apos;re done.</p>
            <span className="scn-btn primary">Turn ScrollStop on</span>
            <span className="scn-btn ghost">Go to home screen</span>
          </div>
        </Phone>
        <p className="scn-cap"><b>Switched off for a restricted app.</b> The app opens, but Instagram and YouTube stay blocked until you switch ScrollStop back on.</p>
      </div>
    </div>
  );
}
