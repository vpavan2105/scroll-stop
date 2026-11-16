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

          <div className="note" style={{ marginTop: 20 }}>
            <Icon name="wrench" color="#F5B13D" />
            <span>
              If an app moves a button it can reappear. ScrollStop notices, notifies you, and lets you fix it:
              open <b>After an app update</b>, tap <b>Re-learn</b>, then tap that button once.
            </span>
          </div>
          <div className="note" style={{ marginTop: 12 }}>
            <Icon name="eye" color="#A3A8B4" />
            <span>Curious what it can see and keep? <Link href="/privacy">Read the privacy page</Link>.</span>
          </div>
        </div>
      </div>
    </>
  );
}
