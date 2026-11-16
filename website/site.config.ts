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
    version: "5.7",
    date: "16 Nov 2026",
    file: "ScrollStop-v5.7.apk",
    notes: [
      "The Reels, Explore and Shorts tabs now stay usable until you hit your daily limit, then they're hidden — so Instagram search keeps working until then.",
      "Switch ScrollStop off any time for a restricted app: while it's off, Instagram and YouTube stay blocked and a reminder brings you back.",
      "Restricted apps like YONO open normally once ScrollStop is switched off — it only watches Instagram, YouTube and Settings.",
      "Simpler setup, one step at a time, with an illustrated how-to for each permission.",
      "No name shown, no internet access: everything stays on your phone.",
    ],
  },
  // Older versions go below, e.g.:
  // { version: "4.1", date: "…", file: "ScrollStop-v4.1.apk", notes: ["…"] },
];

export const site = {
  name: "ScrollStop",
  tagline: "Keep Instagram and YouTube. Lose the endless scroll.",
  description:
    "ScrollStop is a free Android app that puts a daily limit on Reels, Explore and Shorts inside the real Instagram and YouTube apps. Use them up to your limit, then the tab is hidden. No ads, no internet access.",

  /** The live address, e.g. "https://scrollstop.vercel.app" (used for link previews). */
  url: "https://scrollstop.vercel.app",

  minAndroid: "Android 8.0+",

  /**
   * Every release, NEWEST FIRST (edit the list above). The first one is what the
   * Download buttons give. Size and SHA-256 are worked out from each file automatically.
   */
  releases,

  /** Where questions go (shown on the Contact and Privacy pages). */
  contactEmail: "vpktech21@gmail.com",

  /** "Buy me a tea". Fill in whichever you use; empty ones are hidden. */
  support: {
    /** Your UPI ID, e.g. "yourname@okaxis". Shows a QR code and pay buttons. */
    upiId: "",
    /** Name shown in the UPI app when someone pays. */
    upiName: "ScrollStop",
    /** Suggested amounts in rupees. */
    amounts: [30, 50, 100] as number[],
    /** Optional: a Buy Me a Coffee / Ko-fi page for people outside India. */
    internationalUrl: "https://buymeacoffee.com/pavankumarlw",
    internationalLabel: "Buy me a coffee",
  },
};

export const latest: Release = site.releases[0];
