/**
 * Everything you might want to change, in one place.
 * Empty values are hidden on the live site (and flagged while you run `npm run dev`).
 */
export type Release = {
  version: string;
  date: string;
  /** The APK's file name inside /public. */
  file: string;
  /** What changed, one line each. */
  notes: string[];
};

const releases: Release[] = [
  {
    version: "5.1",
    date: "15 Nov 2026",
    file: "ScrollStop-v5.1.apk",
    notes: [
      "New name: ScrollStop (installs as a new app, so remove the old \"Reels Blocker\").",
      "Banking apps like YONO open normally: ScrollStop now only sees Instagram, YouTube and Settings.",
      "YouTube Shorts: the Shorts tab is hidden and Shorts get a daily limit.",
      "A reminder appears when you try to switch protection off.",
      "Clearer setup on Android 13 and newer (the \"Restricted setting\" message).",
    ],
  },
  // Older versions go below, e.g.:
  // { version: "4.1", date: "…", file: "ScrollStop-v4.1.apk", notes: ["…"] },
];

export const site = {
  name: "ScrollStop",
  tagline: "Keep Instagram and YouTube. Lose the endless scroll.",
  description:
    "ScrollStop is a free Android app that hides Reels, Explore and Shorts inside the real Instagram and YouTube apps and gives you daily limits. No ads, no internet access.",

  /** The live address, e.g. "https://scrollstop.vercel.app" (used for link previews). */
  url: "https://scrollstop.vercel.app",

  minAndroid: "Android 8.0+",

  /**
   * Every release, NEWEST FIRST (edit the list above). The first one is what the
   * Download buttons give. Size and SHA-256 are worked out from each file automatically.
   */
  releases,

  /** Where questions go (shown on the Contact and Privacy pages). */
  contactEmail: "",

  /** "Buy me a tea". Fill in whichever you use; empty ones are hidden. */
  support: {
    /** Your UPI ID, e.g. "yourname@okaxis". Shows a QR code and pay buttons. */
    upiId: "",
    /** Name shown in the UPI app when someone pays. */
    upiName: "ScrollStop",
    /** Suggested amounts in rupees. */
    amounts: [30, 50, 100] as number[],
    /** Optional: a Buy Me a Coffee / Ko-fi page for people outside India. */
    internationalUrl: "",
    internationalLabel: "Buy me a coffee",
  },
};

export const latest: Release = site.releases[0];
