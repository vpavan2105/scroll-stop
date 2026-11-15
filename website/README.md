# ScrollStop website

Next.js site for the ScrollStop Android app: one page per topic, the APK download,
version history and a "Buy me a tea" page.

## Settings

Everything you change lives in **`site.config.ts`**:

- `contactEmail`: shown on Contact (with a "Write in Gmail" button) and Privacy
- `support.upiId`: shows UPI pay buttons and a QR code on "Buy me a tea"
- `support.internationalUrl`: optional Buy Me a Coffee / Ko-fi link
- `releases`: the version list, **newest first**. The first entry is what every Download button gives.

Empty values are hidden on the live site. While you run `npm run dev`, a dashed
"Setup:" box shows where something still needs filling in.

## Run locally

```bash
npm install
npm run dev        # http://localhost:3000
```

## Release a new app version

1. Build the APK in Android Studio and copy it into `public/`, e.g. `public/ScrollStop-v5.1.apk`.
2. In `site.config.ts`, add an entry at the **top** of `releases` with the version, date, file name and notes.
3. Deploy (below). Size and SHA-256 are calculated from the file automatically, and older versions stay listed on /versions.

## Deploy to Vercel

```bash
npx vercel --prod
```

The first time, it asks you to log in and to name the project (`scrollstop` gives
`scrollstop.vercel.app` if it's free). After that, the same command updates the site.
`next.config.ts` makes phones download `.apk` files as Android apps.

## Pages

| Route | What |
|---|---|
| `/` | Home: hero, Download, highlights |
| `/features` | Everything ScrollStop does |
| `/how-it-works` | The four jobs, in plain words |
| `/install` | Step-by-step setup |
| `/versions` | Latest version on top, older ones below |
| `/why` | Why it isn't on the Play Store |
| `/privacy` | Privacy policy |
| `/faq` | Questions |
| `/support` | Buy me a tea (UPI) |
| `/contact` | Email via Gmail |

Fonts: Inter and Space Grotesk (SIL Open Font License, see `fonts/OFL-*.txt`).
