import type { Metadata } from "next";
import Link from "next/link";
import { Icon } from "@/components/Icon";
import { PageIntro } from "@/components/PageIntro";
import { DiagScreen, DiagCount, DiagHide, DiagStepAside } from "@/components/HowDiagrams";

export const metadata: Metadata = { title: "How it works" };

const steps = [
  { art: <DiagScreen />, title: "It reads the screen", text: "Android describes every screen as a tree of buttons — the same thing screen readers use. ScrollStop is an Accessibility service, so it can read that tree whenever Instagram or YouTube changes." },
  { art: <DiagCount />, title: "It counts", text: "Each reel or Short you swipe counts as one, and so does each screen of suggested posts. Counts save on your phone and reset at midnight." },
  { art: <DiagHide />, title: "It hides a tab at the limit", text: "Under your daily limit the tabs work normally. Once it's used up, ScrollStop covers that tab with a plain box until midnight. Nothing inside the apps is changed." },
  { art: <DiagStepAside />, title: "It steps aside for restricted apps", text: "Switch ScrollStop off to use an app like YONO that won't run with accessibility on. A light check keeps Instagram and YouTube blocked until you switch it back on." },
];

export default function HowItWorks() {
  return (
    <>
      <PageIntro label="How it works" title="A few small jobs, all on your phone">
        No servers, no account, no internet. ScrollStop only changes what your own screen shows.
      </PageIntro>
      <div className="section">
        <div className="wrap">
          <ol className="steps with-art">
            {steps.map((s) => (
              <li key={s.title}>
                <div>
                  <h3>{s.title}</h3>
                  <p>{s.text}</p>
                </div>
                <div className="step-art">{s.art}</div>
              </li>
            ))}
          </ol>

          <div className="relearn-box">
            <p className="label">After an app update</p>
            <h3 className="rl-title">If a tab comes back, re-teach it in seconds</h3>
            <p className="rl-sub">Instagram and YouTube move their buttons now and then, and a hidden one can slip back. ScrollStop can&apos;t guess the new layout, so instead it notices, tells you, and lets you point the button out once &mdash; all on your phone.</p>
            <div className="relearn">
              <div className="rl-step">
                <span className="rl-ico"><Icon name="info" size={20} /></span>
                <b>It warns you</b>
                <span>A notification: &ldquo;the Reels tab is back&rdquo;.</span>
              </div>
              <span className="rl-arrow" aria-hidden="true">&rarr;</span>
              <div className="rl-step">
                <span className="rl-ico"><Icon name="wrench" size={20} /></span>
                <b>Tap Re-learn</b>
                <span>Open <i>After an app update</i> and tap it.</span>
              </div>
              <span className="rl-arrow" aria-hidden="true">&rarr;</span>
              <div className="rl-step">
                <span className="rl-ico">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><path d="M9 11V6a1.8 1.8 0 013.6 0v5" /><path d="M12.6 11V9a1.8 1.8 0 013.6 0v2" /><path d="M16.2 11v-.5a1.8 1.8 0 013.6 0V15a5 5 0 01-5 5h-2a5 5 0 01-4.4-2.6L5 13.5a1.8 1.8 0 013.1-1.8L9 13" /></svg>
                </span>
                <b>Point to the button</b>
                <span>Tap it once in the app, within 30s.</span>
              </div>
              <span className="rl-arrow" aria-hidden="true">&rarr;</span>
              <div className="rl-step done">
                <span className="rl-ico"><Icon name="check" size={20} strokeWidth={3} /></span>
                <b>Hidden again</b>
                <span>ScrollStop has re-learned it.</span>
              </div>
            </div>
          </div>

          <div className="note" style={{ marginTop: 16 }}>
            <Icon name="eye" color="#A3A8B4" />
            <span>Curious what it can see and keep? <Link href="/privacy">Read the privacy page</Link>.</span>
          </div>
        </div>
      </div>
    </>
  );
}
