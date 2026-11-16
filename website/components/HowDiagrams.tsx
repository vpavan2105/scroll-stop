/** Small, robust drawn diagrams for the How it works steps. */

/** 1. Reads the screen as a tree of buttons. */
export function DiagScreen() {
  return (
    <div className="hd" role="img" aria-label="Android describes the screen as a tree: Instagram, with Home, Reels and Explore buttons; ScrollStop reads it.">
      <div className="hd-tree">
        <span className="hd-node root">Instagram screen</span>
        <span className="hd-stem" aria-hidden="true" />
        <div className="hd-kids">
          <span className="hd-node">Home</span>
          <span className="hd-node mark">Reels</span>
          <span className="hd-node mark">Explore</span>
        </div>
      </div>
    </div>
  );
}

/** 2. Counts each reel / suggested post / Short. */
export function DiagCount() {
  const c = 2 * Math.PI * 26;
  const f = 12 / 30;
  return (
    <div className="hd" role="img" aria-label="A counter showing 12 of 30 reels used today.">
      <div className="hd-ring">
        <svg width="84" height="84" viewBox="0 0 64 64" aria-hidden="true">
          <circle cx="32" cy="32" r="26" fill="none" stroke="#262A33" strokeWidth="6" />
          <circle cx="32" cy="32" r="26" fill="none" stroke="#F5B13D" strokeWidth="6" strokeLinecap="round"
            strokeDasharray={`${c * f} ${c}`} transform="rotate(-90 32 32)" />
        </svg>
        <b>12<span>/30</span></b>
      </div>
      <span className="hd-cap">reels today</span>
    </div>
  );
}

function Tabs({ hide }: { hide?: boolean }) {
  return (
    <span className="dt" aria-hidden="true">
      {[0, 1, 2, 3, 4].map((i) => (
        <i key={i} className={`dt-i${hide && (i === 1 || i === 2) ? " cov" : ""}`} />
      ))}
    </span>
  );
}

/** 3. Covers the tab once the limit is used up. */
export function DiagHide() {
  return (
    <div className="hd" role="img" aria-label="Under the limit the tabs show; over the limit the Reels and Explore tabs are covered.">
      <div className="hd-rows">
        <div className="hd-brow"><Tabs /><span className="hd-chip mint">tabs on</span></div>
        <div className="hd-brow"><Tabs hide /><span className="hd-chip amber">2 hidden</span></div>
      </div>
    </div>
  );
}

/** 4. Steps aside for a restricted app. */
export function DiagStepAside() {
  return (
    <div className="hd" role="img" aria-label="With ScrollStop switched off, a restricted app opens while Instagram and YouTube stay blocked.">
      <div className="hd-rows">
        <span className="hd-pill off">ScrollStop off</span>
        <div className="hd-arow"><b>YONO</b><i className="ok">opens</i></div>
        <div className="hd-arow"><b>Instagram</b><i className="no">blocked</i></div>
        <div className="hd-arow"><b>YouTube</b><i className="no">blocked</i></div>
      </div>
    </div>
  );
}
