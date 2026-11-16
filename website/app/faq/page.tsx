import type { Metadata } from "next";
import Link from "next/link";
import { PageIntro } from "@/components/PageIntro";

export const metadata: Metadata = { title: "FAQ" };

export default function Faq() {
  return (
    <>
      <PageIntro label="FAQ" title="Questions" />
      <div className="section">
        <div className="wrap" style={{ maxWidth: 820 }}>
          <details>
            <summary>Does it block ads?</summary>
            <p>No. It doesn&apos;t look for ads at all. It only puts a daily limit on reels, suggested posts and Shorts, and hides the Reels, Explore and Shorts tabs once you&apos;ve used that limit up.</p>
          </details>
          <details>
            <summary>Can I still search for people on Instagram?</summary>
            <p>Yes, until you reach your daily limit. The Explore / Search tab stays usable up to the suggested-posts limit, so you can look people up; after the limit it&apos;s hidden for the rest of the day. You can always search on instagram.com in a browser too.</p>
          </details>
          <details>
            <summary>A tab is showing again. What do I do?</summary>
            <p>Instagram or YouTube probably updated. Open ScrollStop › <b>After an app update</b>, tap <b>Re-learn</b> next to that button, then tap the button once in the app within 30 seconds.</p>
          </details>
          <details>
            <summary>Will my restricted apps still work?</summary>
            <p>Yes. Some apps, like YONO, refuse to open while any accessibility app is switched on — ScrollStop calls these restricted apps. When you need one, switch ScrollStop off from its main screen (or in Accessibility settings) — no wait. While it&apos;s off, a lightweight check keeps Instagram and YouTube blocked and reminds you to turn ScrollStop back on, so switching off for a restricted app isn&apos;t a way back to endless scrolling.</p>
          </details>
          <details>
            <summary>Does it drain the battery?</summary>
            <p>No. While it&apos;s on it only reacts when Instagram or YouTube is on screen, and sleeps the rest of the time. While it&apos;s switched off for a restricted app, the background check wakes briefly every second or so to see which app is open, then sleeps again.</p>
          </details>
          <details>
            <summary>How do I remove it?</summary>
            <p>Uninstall it like any app: long-press the icon › Uninstall. That always works, with no wait. To only pause it for a restricted app, switch it off from the main screen — also no wait. The 5-minute wait only applies if you try to remove a permission ScrollStop relies on, or raise a limit.</p>
          </details>
          <details>
            <summary>Does it update itself?</summary>
            <p>No, and on purpose. ScrollStop has no internet access and isn&apos;t on the Play Store, so it can&apos;t update in the background &mdash; nothing about you ever leaves your phone. When there&apos;s a new version, come back to this site, download it and install over the old one; your limits and counts are kept. The <Link href="/versions">Versions page</Link> shows what changed.</p>
          </details>
          <details>
            <summary>Does it work on iPhone?</summary>
            <p>No. iPhones don&apos;t let apps see or change other apps&apos; screens, so this approach only works on Android.</p>
          </details>
          <details>
            <summary>What doesn&apos;t work yet?</summary>
            <ul>
              <li>The Following switch and the permission reminder look for English words, so they need Instagram and your phone in English.</li>
              <li>Counts are close, not exact: a swipe back to a video you&apos;ve seen counts again.</li>
              <li>Instagram and YouTube in a web browser aren&apos;t covered, only the apps.</li>
            </ul>
          </details>
          <details>
            <summary>I have another question</summary>
            <p>Send it through the <Link href="/contact">contact page</Link>.</p>
          </details>
        </div>
      </div>
    </>
  );
}
