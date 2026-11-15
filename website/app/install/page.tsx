import type { Metadata } from "next";
import Link from "next/link";
import { Icon } from "@/components/Icon";
import { PageIntro } from "@/components/PageIntro";
import { latest } from "@/site.config";

export const metadata: Metadata = { title: "Install" };

export default function Install() {
  return (
    <>
      <PageIntro label="Install" title="Set it up in about two minutes">
        Android asks a few extra questions because ScrollStop doesn&apos;t come from the Play Store and uses Accessibility. Each one is normal for this kind of app.
      </PageIntro>
      <div className="section">
        <div className="wrap">
          <ol className="steps">
            <li><div><h3>Download and open the APK</h3><p>Tap <b>Download</b> at the top of this site, then open the file and choose <b>Install</b>. If asked, allow <b>Install unknown apps</b> for your browser or file manager.</p></div></li>
            <li><div><h3>If Play Protect blocks or warns you</h3><p>In India, Play Protect blocks apps that use Accessibility when they&apos;re installed from a browser, WhatsApp or a file manager. You&apos;ll see <b>App blocked to protect your device</b>. To install ScrollStop, open <span className="path">Play Store › your profile picture › Play Protect › ⚙</span>, turn off <b>Scan apps with Play Protect</b>, install the APK, then turn it back on straight away. If you only get a warning, tap <b>More details › Install anyway</b>.</p></div></li>
            <li><div><h3>Turn ScrollStop on</h3><p>Open ScrollStop and follow its setup, or go to <span className="path">Settings › Accessibility › ScrollStop</span> and switch it on.</p></div></li>
            <li><div><h3>If it says &quot;Restricted setting&quot; (Android 13 and newer)</h3><p>That&apos;s expected for apps installed from a file. Close the message, go to <span className="path">Settings › Apps › ScrollStop › ⋮ › Allow restricted settings</span>, confirm, then switch ScrollStop on again. The ⋮ option only appears after you&apos;ve seen the message once.</p></div></li>
            <li><div><h3>Allow notifications</h3><p>So ScrollStop can tell you if protection ever stops, or if Instagram or YouTube changes its layout.</p></div></li>
            <li><div><h3>Xiaomi, Oppo, Vivo, Realme, Samsung: stop the phone freezing it</h3><p>ScrollStop works while Instagram or YouTube is on screen. These phones sometimes freeze apps that have no screen of their own, to save battery, and then the hidden tabs come back. Go to <span className="path">Settings › Apps › ScrollStop › Battery</span> and choose <b>No restrictions</b> (it may be called Unrestricted or Allow background activity). It won&apos;t use more battery: ScrollStop sleeps whenever you&apos;re not using those two apps.</p></div></li>
          </ol>
          <div className="note" style={{ marginTop: 20 }}>
            <Icon name="info" color="#A3A8B4" />
            <span>
              Coming from the old &quot;Reels Blocker&quot;? ScrollStop {latest.version} installs as a separate app, so uninstall Reels Blocker first.
              See what changed on the <Link href="/versions">Versions page</Link>.
            </span>
          </div>
        </div>
      </div>
    </>
  );
}
