import type { Metadata } from "next";
import Link from "next/link";
import { DevNote, PageIntro } from "@/components/PageIntro";
import { latest, site } from "@/site.config";

export const metadata: Metadata = { title: "Privacy policy", description: "ScrollStop collects no data and has no internet access." };

export default function Privacy() {
  return (
    <>
      <PageIntro label="Privacy policy" title="Nothing leaves your phone">
        Last updated {latest.date}. Short version: ScrollStop collects nothing, shares nothing and cannot connect to the internet.
      </PageIntro>
      <div className="section">
        <div className="wrap" style={{ maxWidth: 760 }}>
          <h2>The Accessibility service</h2>
          <p className="sub">ScrollStop uses Android&apos;s Accessibility service, which lets it see the layout of what is on your screen. It uses this only to:</p>
          <ul className="check">
            {[
              "find and cover the Reels and Explore buttons in Instagram and the Shorts button in YouTube,",
              "open Instagram's Following feed when you open Instagram,",
              "count reels, Shorts and suggested posts toward the daily limits you set,",
              "show a reminder on the Settings page that switches ScrollStop off.",
            ].map((t) => <li key={t}>{t}</li>)}
          </ul>
          <p className="sub" style={{ marginTop: 16 }}>It ignores every other app. Screen content is looked at in memory, in the moment, and is never stored or sent anywhere.</p>

          <h2>What is stored on your phone</h2>
          <ul className="check">
            <li>Today&apos;s counts and your daily limits.</li>
            <li>Button names you re-learned after an app update.</li>
            <li>A screen-layout capture, only when you tap &quot;Capture screen&quot; yourself. It can include text that was on screen, stays in the app&apos;s private storage, and is only shared if you choose &quot;Share capture&quot;.</li>
          </ul>
          <p className="sub" style={{ marginTop: 16 }}>All of this stays inside the app and is deleted when you uninstall it.</p>

          <h2>What is collected or shared</h2>
          <p className="sub">Nothing. ScrollStop has no internet permission, so it cannot send data anywhere. There is no account, no analytics, no crash reporting and no advertising.</p>

          <h2>Permissions</h2>
          <ul className="check">
            <li><b>Accessibility service</b>: to see and cover parts of Instagram and YouTube, as described above.</li>
            <li><b>Notifications</b>: to tell you if protection stops or an app&apos;s layout changed.</li>
            <li><b>Run at startup</b>: so the check that protection is still running continues after a restart.</li>
          </ul>

          <h2 style={{ marginTop: 32 }}>Changes and contact</h2>
          <p className="sub">
            If this policy changes, the new version will be posted here with a new date.{" "}
            {site.contactEmail ? <>Questions: <Link href="/contact">contact page</Link>.</> : null}
          </p>
          {!site.contactEmail && <DevNote>set contactEmail in site.config.ts to show the contact line.</DevNote>}
        </div>
      </div>
    </>
  );
}
