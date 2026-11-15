import type { Metadata } from "next";
import { Icon } from "@/components/Icon";
import { DevNote, PageIntro } from "@/components/PageIntro";
import { getApkInfo } from "@/lib/apk";
import { site, type Release } from "@/site.config";

export const metadata: Metadata = { title: "Versions" };

function ReleaseCard({ release, isLatest }: { release: Release; isLatest: boolean }) {
  const apk = getApkInfo(release.file);
  return (
    <div className="card">
      <div className="btn-row" style={{ justifyContent: "space-between" }}>
        <div>
          <h3 style={{ margin: 0, fontSize: 20 }}>
            Version {release.version}
            {isLatest ? <span className="chip" style={{ marginLeft: 10, verticalAlign: "middle" }}>Latest</span> : null}
          </h3>
          <div className="meta">
            <span>{release.date}</span>
            {apk ? <span>{apk.sizeLabel}</span> : null}
            <span>{site.minAndroid}</span>
          </div>
        </div>
        {apk ? (
          <a className={`btn ${isLatest ? "btn-primary" : "btn-secondary"} btn-small`} href={apk.href} download>
            <Icon name="download" size={18} strokeWidth={2.2} />
            Download {release.version}
          </a>
        ) : null}
      </div>
      <ul className="check" style={{ marginTop: 16 }}>
        {release.notes.map((n) => (
          <li key={n}><Icon name="check" size={16} strokeWidth={3} color="#5BD6A5" />{n}</li>
        ))}
      </ul>
      {apk ? <div className="hash">SHA-256: {apk.sha256}</div> : <DevNote>put {release.file} in the public folder.</DevNote>}
    </div>
  );
}

export default function Versions() {
  const [latest, ...older] = site.releases;
  return (
    <>
      <PageIntro label="Versions" title="Every version of ScrollStop">
        The Download button at the top always gives you the latest. ScrollStop doesn&apos;t update by itself, so check back here for new versions.
      </PageIntro>
      <div className="section">
        <div className="wrap">
          <ReleaseCard release={latest} isLatest />

          <h2 style={{ marginTop: 40 }}>Older versions</h2>
          {older.length === 0 ? (
            <p className="sub">None yet. This is the first ScrollStop release.</p>
          ) : (
            <div className="grid" style={{ gap: 12 }}>
              {older.map((r) => <ReleaseCard key={r.version} release={r} isLatest={false} />)}
            </div>
          )}

          <p className="fine">
            The SHA-256 line is a fingerprint of the file. If it matches what your phone or computer calculates, the file hasn&apos;t been changed.
          </p>
        </div>
      </div>
    </>
  );
}
