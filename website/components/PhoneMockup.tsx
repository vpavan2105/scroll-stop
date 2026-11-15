/** A small drawing of the app's Home screen (sample numbers). */
function Ring({ value, fraction, label }: { value: number; fraction: number; label: string }) {
  const c = 2 * Math.PI * 28;
  return (
    <div>
      <div className="ring">
        <svg width="66" height="66" viewBox="0 0 66 66" aria-hidden="true">
          <circle cx="33" cy="33" r="28" fill="none" stroke="#262A33" strokeWidth="6" />
          <circle cx="33" cy="33" r="28" fill="none" stroke="#F5B13D" strokeWidth="6" strokeLinecap="round"
            strokeDasharray={`${c * fraction} ${c}`} transform="rotate(-90 33 33)" />
        </svg>
        <b>{value}</b>
      </div>
      <div className="rl">{label}</div>
    </div>
  );
}

export function PhoneMockup() {
  return (
    <div className="phone" role="img"
      aria-label="ScrollStop's home screen: protection on, today's counts for reels, suggested posts and Shorts, and what's hidden in Instagram and YouTube">
      <div className="screen">
        <div className="srow"><span className="sbrand">ScrollStop</span><span className="chip">Protection on</span></div>
        <p className="stoday">Today</p>
        <div className="scard">
          <div className="rings">
            <Ring value={12} fraction={12 / 30} label="Reels" />
            <Ring value={8} fraction={8 / 30} label="Suggested" />
            <Ring value={5} fraction={5 / 30} label="Shorts" />
          </div>
        </div>
        <div className="scard slist">
          <div><span>Reels tab</span><span className="chip">Hidden</span></div>
          <div><span>Explore tab</span><span className="chip">Hidden</span></div>
          <div><span>Shorts tab</span><span className="chip">Hidden</span></div>
          <div><span>Suggested posts</span><span style={{ color: "var(--amber)", fontWeight: 600 }}>30 a day</span></div>
        </div>
      </div>
    </div>
  );
}
