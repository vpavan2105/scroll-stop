import type { Metadata } from "next";
import Link from "next/link";
import { Icon } from "@/components/Icon";
import { PageIntro } from "@/components/PageIntro";
import { AndroidSetting } from "@/components/mock";
import { latest } from "@/site.config";

export const metadata: Metadata = { title: "Install" };

export default function Install() {
  return (
    <>
      <PageIntro label="Install" title="Set it up in about two minutes">
        A few extra Android prompts are normal for an app that isn&apos;t from the Play Store. ScrollStop&apos;s own setup walks you through them in order.
      </PageIntro>
      <div className="section">
        <div className="wrap">
          <ol className="steps with-art">
            <li>
              <div>
                <h3>Download and open the APK</h3>
                <p>Tap <b>Download</b>, open the file, choose <b>Install</b>. Allow <b>Install unknown apps</b> if asked.</p>
              </div>
              <div className="step-art">
                <AndroidSetting title="Install unknown apps" rows={[{ label: "Allow from this source", sub: "Your browser or Files", toggle: true, hl: true }]} />
              </div>
            </li>

            <li>
              <div>
                <h3>If Play Protect blocks it</h3>
                <p>In India it may say <b>App blocked</b>. Open <span className="path">Play Store › profile › Play Protect › ⚙</span>, turn <b>Scan apps</b> off, install, then turn it back on. A warning instead? Tap <b>More details › Install anyway</b>.</p>
              </div>
              <div className="step-art">
                <AndroidSetting title="Play Protect settings" rows={[{ label: "Scan apps with Play Protect", toggle: false, hl: true }, { label: "Improve harmful app detection", toggle: false }]} />
              </div>
            </li>

            <li>
              <div>
                <h3>Allow restricted settings</h3>
                <p>Android 13+. Open <span className="path">Settings › Apps › ScrollStop › ⋮</span> and tap <b>Allow restricted settings</b>. The ⋮ option shows up only after you&apos;ve tried an Accessibility setting once — ScrollStop sends you there first.</p>
              </div>
              <div className="step-art">
                <AndroidSetting title="ScrollStop · ⋮" rows={[{ label: "Allow restricted settings", hl: true, chevron: true }, { label: "Uninstall", chevron: true }]} />
              </div>
            </li>

            <li>
              <div>
                <h3>Usage access &amp; Display over other apps</h3>
                <p>These let ScrollStop tell which app is open and show its reminder while switched off. Grant both from setup. Removing either later waits 5 minutes — they don&apos;t read your social media.</p>
              </div>
              <div className="step-art">
                <AndroidSetting title="Usage access" rows={[{ label: "ScrollStop", sub: "Permit usage access", toggle: true, hl: true }]} />
                <AndroidSetting title="Display over other apps" rows={[{ label: "ScrollStop", sub: "Allow", toggle: true, hl: true }]} />
              </div>
            </li>

            <li>
              <div>
                <h3>Allow notifications</h3>
                <p>So ScrollStop can warn you if protection stops or an app changes its layout. You can skip this one.</p>
              </div>
              <div className="step-art">
                <AndroidSetting title="Notifications" rows={[{ label: "ScrollStop", toggle: true, hl: true }]} />
              </div>
            </li>

            <li>
              <div>
                <h3>Turn ScrollStop on</h3>
                <p>Last step: <span className="path">Settings › Accessibility › ScrollStop</span> and switch it on. Switch it off here any time for a restricted app — no wait.</p>
              </div>
              <div className="step-art">
                <AndroidSetting title="Accessibility" rows={[{ label: "ScrollStop", sub: "Installed apps", toggle: true, hl: true }]} />
              </div>
            </li>

            <li>
              <div>
                <h3>Phones that freeze apps</h3>
                <p>Xiaomi, Oppo, Vivo, Realme, Samsung. Set <span className="path">ScrollStop › Battery</span> to <b>No restrictions</b> so it isn&apos;t frozen. It won&apos;t use more battery.</p>
              </div>
              <div className="step-art">
                <AndroidSetting title="ScrollStop · Battery" rows={[{ label: "No restrictions", sub: "Recommended", hl: true, chevron: true }, { label: "Optimised", chevron: true }]} />
              </div>
            </li>
          </ol>

          <div className="note" style={{ marginTop: 20 }}>
            <Icon name="download" color="#F5B13D" />
            <span>
              It won&apos;t update itself &mdash; ScrollStop has no internet access, so nothing about you ever leaves your phone.
              When a new version is out, download it here and install over the old one; your limits and counts stay.
            </span>
          </div>
          <div className="note" style={{ marginTop: 12 }}>
            <Icon name="info" color="#A3A8B4" />
            <span>
              Coming from the old &quot;Reels Blocker&quot;? ScrollStop {latest.version} installs separately, so uninstall Reels Blocker first.
              See the <Link href="/versions">Versions page</Link>.
            </span>
          </div>
        </div>
      </div>
    </>
  );
}
