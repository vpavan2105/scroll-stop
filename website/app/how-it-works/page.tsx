import type { Metadata } from "next";
import Link from "next/link";
import { Icon } from "@/components/Icon";
import { PageIntro } from "@/components/PageIntro";

export const metadata: Metadata = { title: "How it works" };

const steps = [
  { title: "It sees the screen", text: "Android describes every screen as a tree of buttons and lists. That's how screen readers read apps aloud. ScrollStop is an Accessibility service too, so it can read that tree whenever Instagram or YouTube changes." },
  { title: "It finds the buttons", text: "It looks for the Reels, Explore and Shorts buttons by their internal names, with their labels (\"Reels\", \"Shorts\") as a back-up." },
  { title: "It covers them", text: "It draws a plain box over each button in a special overlay window that only Accessibility services can use. Nothing inside Instagram or YouTube is changed." },
  { title: "It counts", text: "Each swipe in a reel or Short counts as one, and so does every screen of suggested posts you scroll past. Today's counts are saved on your phone and reset at midnight." },
];

export default function HowItWorks() {
  return (
    <>
      <PageIntro label="How it works" title="Four small jobs, all on your phone">
        No servers, no account, no internet. ScrollStop only changes what your own screen shows.
      </PageIntro>
      <div className="section">
        <div className="wrap">
          <div className="grid g2">
            {steps.map((s, i) => (
              <div className="card" key={s.title}>
                <div className="num">{i + 1}</div>
                <h3>{s.title}</h3>
                <p>{s.text}</p>
              </div>
            ))}
          </div>

          <div className="note" style={{ marginTop: 20 }}>
            <Icon name="wrench" color="#F5B13D" />
            <span>
              Apps change their layout now and then, and a button can show up again. ScrollStop checks for
              this, sends a notification, and lets you fix it yourself: open <b>After an app update</b>, tap{" "}
              <b>Re-learn</b>, then tap that button once.
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
