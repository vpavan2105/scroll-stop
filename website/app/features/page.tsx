import type { Metadata } from "next";
import { Icon, type IconName } from "@/components/Icon";
import { PageIntro } from "@/components/PageIntro";
import { NavIcon } from "@/components/mock";

export const metadata: Metadata = { title: "Features" };

const features: { icon: IconName; title: string; text: string }[] = [
  { icon: "clock", title: "Limit, then hidden", text: "Tabs work up to a daily limit, then hide till midnight." },
  { icon: "following", title: "Opens on Following", text: "Instagram starts on the people you actually follow." },
  { icon: "clock", title: "Three daily limits", text: "Reels, suggested posts and Shorts, counted on their own." },
  { icon: "shield", title: "Restricted apps work", text: "Switch off for apps like YONO; Reels and Shorts stay blocked." },
  { icon: "lock", title: "A gentle lock", text: "Off and uninstall are free. Changing a limit waits 5 minutes." },
  { icon: "wrench", title: "Self-repairs", text: "If an app moves a button, re-learn it in one tap." },
];

function MiniBar({ over }: { over?: boolean }) {
  return (
    <div className="fbar">
      <NavIcon name="home" /><NavIcon name="search" covered={over} /><NavIcon name="reels" covered={over} /><NavIcon name="plus" /><NavIcon name="user" />
    </div>
  );
}

export default function Features() {
  return (
    <>
      <PageIntro label="Features" title="What ScrollStop does">
        It stays inside the official apps. Chats, Stories and the people you follow always work.
      </PageIntro>
      <div className="section">
        <div className="wrap">
          {/* The whole idea, in one picture */}
          <div className="fdiagram">
            <div className="fstate">
              <MiniBar />
              <span className="flabel"><b>Under the limit</b> · every tab works</span>
            </div>
            <Icon name="arrow" size={22} />
            <div className="fstate">
              <MiniBar over />
              <span className="flabel"><b>Over the limit</b> · Reels &amp; Explore hidden</span>
            </div>
          </div>

          <div className="grid g3" style={{ marginTop: 28 }}>
            {features.map((f) => (
              <div className="card fcard" key={f.title}>
                <div className="tile"><Icon name={f.icon} size={22} /></div>
                <h3>{f.title}</h3>
                <p>{f.text}</p>
              </div>
            ))}
          </div>

          <div className="scard flimits" style={{ marginTop: 28 }}>
            <span className="limrow"><b>30</b> reels a day</span>
            <span className="limrow"><b>30</b> suggested posts a day</span>
            <span className="limrow"><b>30</b> Shorts a day</span>
            <span className="limnote">Set each from 1 to 50 · all reset at midnight</span>
          </div>

          <div className="quote" style={{ marginTop: 28 }}>
            <strong>This app isn&apos;t controlling you. Social media is.</strong>
          </div>
        </div>
      </div>
    </>
  );
}
