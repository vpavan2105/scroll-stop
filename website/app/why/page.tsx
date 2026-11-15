import type { Metadata } from "next";
import { Icon } from "@/components/Icon";
import { PageIntro } from "@/components/PageIntro";

export const metadata: Metadata = { title: "Why not the Play Store" };

export default function Why() {
  return (
    <>
      <PageIntro label="Why not the Play Store" title="A personal project, on purpose" />
      <div className="section">
        <div className="wrap">
          <div className="card" style={{ marginBottom: 16 }}>
            <h3>Why I built it</h3>
            <p>I didn&apos;t want to quit Instagram. I use it to chat with friends and to see what the people I follow are up to. The problem was the endless scrolling: open it for one message, look up 40 minutes later. ScrollStop keeps the useful parts, chats and the people you follow, and blocks only the scrolling.</p>
          </div>
          <div className="grid g2">
            <div className="card">
              <h3>The strict lock isn&apos;t allowed there</h3>
              <p>Google Play doesn&apos;t let apps use Accessibility to stop you switching an app off. ScrollStop&apos;s 10-minute wait is its strongest feature, so a Play Store version would have to drop it.</p>
            </div>
            <div className="card">
              <h3>It was built to learn</h3>
              <p>ScrollStop started as a project to understand how Android apps work: Accessibility services, overlays, background checks. Sharing it as a file keeps it simple and free, with no account, store or review process.</p>
            </div>
          </div>
          <div className="note" style={{ marginTop: 16 }}>
            <Icon name="info" color="#A3A8B4" />
            <span>Because it isn&apos;t from the Play Store, it doesn&apos;t update by itself. Come back to this site for new versions, and only install ScrollStop from here.</span>
          </div>
        </div>
      </div>
    </>
  );
}
