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
            <p>No. It doesn&apos;t look for ads at all. It only covers the Reels, Explore and Shorts tabs, and the suggested feed once you&apos;ve reached your own daily limit.</p>
          </details>
          <details>
            <summary>A tab is showing again. What do I do?</summary>
            <p>Instagram or YouTube probably updated. Open ScrollStop › <b>After an app update</b>, tap <b>Re-learn</b> next to that button, then tap the button once in the app within 30 seconds.</p>
          </details>
          <details>
            <summary>Will my banking apps still work?</summary>
            <p>Yes, from version 5.1. Banking apps such as YONO refuse to open while an accessibility app they don&apos;t recognise can see them. ScrollStop now only receives Instagram, YouTube and Settings, so your banking apps are invisible to it. If one still complains, tell me which app on the <Link href="/contact">Contact page</Link>.</p>
          </details>
          <details>
            <summary>Does it drain the battery?</summary>
            <p>No. It only reacts while Instagram or YouTube is on screen, and sleeps the rest of the time. A small check every 15 minutes or so makes sure protection is still running.</p>
          </details>
          <details>
            <summary>How do I remove it?</summary>
            <p>Uninstall it like any app: long-press the icon › Uninstall. That always works, with no wait. To only pause it, use <b>Request unlock</b> in the app, wait 10 minutes, then switch it off in Accessibility settings.</p>
          </details>
          <details>
            <summary>How do I update it?</summary>
            <p>Download the latest version from this site and install it over the old one. Your limits and counts are kept. See the <Link href="/versions">Versions page</Link> for what changed.</p>
          </details>
          <details>
            <summary>Does it work on iPhone?</summary>
            <p>No. iPhones don&apos;t let apps see or change other apps&apos; screens, so this approach only works on Android.</p>
          </details>
          <details>
            <summary>What doesn&apos;t work yet?</summary>
            <ul>
              <li>The Following switch and the Settings reminder look for English words, so they need Instagram and your phone in English.</li>
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
