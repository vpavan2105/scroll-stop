import type { Metadata } from "next";
import { Icon, type IconName } from "@/components/Icon";
import { PageIntro } from "@/components/PageIntro";

export const metadata: Metadata = { title: "Features" };

const features: { icon: IconName; title: string; text: string }[] = [
  { icon: "reelsOff", title: "Reels, Explore and Shorts tabs hidden", text: "ScrollStop covers those buttons in the bottom bar with a plain box in the app's own colour. The box swallows taps, so the button is simply gone." },
  { icon: "following", title: "Instagram opens on Following", text: "Each time you open Instagram it switches to the Following feed: only accounts you follow, newest first. Press Back for the normal Home with the bottom bar." },
  { icon: "clock", title: "Daily limit for suggested posts", text: "On the normal Home feed you can scroll a set number of suggested posts a day. Then the feed is covered, and one tap takes you to Following." },
  { icon: "clock", title: "Daily limit for reels and Shorts", text: "Reels friends send you, and Shorts you open from YouTube's Home feed, count toward their own daily limits. Each swipe is one." },
  { icon: "lock", title: "A strict lock", text: "Switching protection off, or raising a limit, needs a 10-minute wait first. Lowering a limit always works, and so does uninstalling." },
  { icon: "wrench", title: "Repairs itself after app updates", text: "If Instagram or YouTube moves a button, ScrollStop notices and notifies you. Tap Re-learn, then tap that button once, and it's fixed." },
];

export default function Features() {
  return (
    <>
      <PageIntro label="Features" title="What ScrollStop does">
        Everything happens inside the official apps. Chats, Stories, profiles, search and posts from people you follow keep working.
      </PageIntro>
      <div className="section">
        <div className="wrap">
          <div className="grid g3">
            {features.map((f) => (
              <div className="card" key={f.title}>
                <div className="tile"><Icon name={f.icon} size={22} /></div>
                <h3>{f.title}</h3>
                <p>{f.text}</p>
              </div>
            ))}
          </div>

          <div className="quote" style={{ marginTop: 32 }}>
            <p>When you try to switch it off, it reminds you:</p>
            <strong>This app isn&apos;t controlling you. Social media is.</strong>
          </div>

          <div className="card" style={{ marginTop: 32 }}>
            <h3>All limits at a glance</h3>
            <ul className="check" style={{ marginTop: 12 }}>
              {["Full-screen reels: 30 a day", "Suggested posts on Instagram Home: 30 a day", "YouTube Shorts: 30 a day", "Each one adjustable from 1 to 50, and all of them reset at midnight"].map((t) => (
                <li key={t}><Icon name="check" size={16} strokeWidth={3} color="#5BD6A5" />{t}</li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </>
  );
}
