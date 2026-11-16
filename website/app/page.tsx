import Link from "next/link";
import { Icon, type IconName } from "@/components/Icon";
import { PhoneMockup } from "@/components/PhoneMockup";
import { ScenarioPhones } from "@/components/ScenarioPhones";
import { getApkInfo } from "@/lib/apk";
import { latest, site } from "@/site.config";

const highlights: { icon: IconName; title: string; text: string }[] = [
  { icon: "clock", title: "Limits, not blackouts", text: "Reels, Explore and Shorts work up to your daily limit. Once you hit it, that tab is hidden until tomorrow." },
  { icon: "following", title: "Opens Following", text: "Instagram starts on your Following feed: only people you follow, no suggestions." },
  { icon: "reelsOff", title: "Search still works", text: "The Explore tab stays usable up to the limit, so you can still look people up. Chats and Stories always work." },
  { icon: "lock", title: "Restriction friendly", text: "Switch it off any time for a restricted app. While off, Instagram and YouTube stay blocked until you turn it back on." },
];

export default function Home() {
  const apk = getApkInfo(latest.file);
  return (
    <>
      <div className="hero">
        <div className="wrap">
          <div>
            <span className="eyebrow"><Icon name="shield" size={16} />Free · No ads · No internet access</span>
            <h1>Keep Instagram and YouTube. <em>Lose the endless scroll.</em></h1>
            <p className="lead">
              ScrollStop lets you use Reels, Explore and Shorts up to a daily limit, then hides them. It opens your
              Following feed, and chats, Stories and the people you follow keep working.
            </p>
            <div className="btn-row">
              <a className="btn btn-primary" href={`/${latest.file}`} download>
                <Icon name="download" strokeWidth={2.2} />
                Download for Android
              </a>
              <Link className="btn btn-secondary" href="/install">How to install</Link>
            </div>
            <p className="fine">
              Version {latest.version}{apk ? ` · ${apk.sizeLabel}` : ""} · {site.minAndroid}.{" "}
              Not on the Play Store, it's a personal project. <Link href="/why">Here's why</Link>.
            </p>
          </div>
          <PhoneMockup />
        </div>
      </div>

      <div className="band">
        <div className="wrap">
          <p className="label">What it does</p>
          <h2>It removes the addictive parts, not the app</h2>
          <p className="sub">You keep using the official Instagram and YouTube apps, with no other app to log in to.</p>
          <div className="grid g4">
            {highlights.map((h) => (
              <div className="card" key={h.title}>
                <div className="tile"><Icon name={h.icon} size={22} /></div>
                <h3>{h.title}</h3>
                <p>{h.text}</p>
              </div>
            ))}
          </div>
          <Link className="more" href="/features">All features <Icon name="arrow" size={16} /></Link>
        </div>
      </div>

      <div className="section">
        <div className="wrap">
          <p className="label">What you&apos;ll see</p>
          <h2>Three everyday moments</h2>
          <p className="sub">Nothing inside Instagram or YouTube is changed &mdash; ScrollStop only draws over the parts you&apos;ve asked it to.</p>
          <ScenarioPhones />
        </div>
      </div>

      <div className="band">
        <div className="wrap">
          <div className="quote">
            <p>A gentle reminder when you try to weaken it:</p>
            <strong>This app isn&apos;t controlling you. Social media is.</strong>
            <small>You set these limits for yourself. ScrollStop just remembers them when it&apos;s hard to.</small>
          </div>
        </div>
      </div>

      <div className="band">
        <div className="wrap grid g3">
          <Link className="card" href="/how-it-works">
            <div className="tile"><Icon name="eye" size={22} /></div>
            <h3>How it works</h3>
            <p>A few small jobs, all on your phone.</p>
          </Link>
          <Link className="card" href="/install">
            <div className="tile"><Icon name="download" size={22} /></div>
            <h3>Install in two minutes</h3>
            <p>Every Android prompt explained.</p>
          </Link>
          <Link className="card" href="/support">
            <div className="tile amber"><Icon name="tea" size={22} /></div>
            <h3>Buy me a tea</h3>
            <p>ScrollStop is free. If it gave you time back, say thanks.</p>
          </Link>
        </div>
      </div>
    </>
  );
}
