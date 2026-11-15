# ScrollStop

Keep Instagram and YouTube. Lose the endless scroll.

I still wanted Instagram to chat with friends and to see what the people I follow are posting, but kept
losing time to endless Reels. ScrollStop is an Android app that blocks only the scrolling, inside the
official apps, with no second login:

- hides the **Reels** and **Explore** tabs in Instagram and opens the **Following** feed
- daily limits for reels, suggested posts and **YouTube Shorts** (default 30 each, adjustable 1–50)
- hides the YouTube **Shorts** tab
- a 10-minute wait before the blocker can be switched off (uninstalling is always allowed)
- warns you with a notification if Android stops the service
- only sees Instagram, YouTube and Settings, so banking apps (YONO etc.) work normally
- no internet permission, no account, no ads: everything stays on the phone

## Repository

```
android/   the app (Kotlin, Android Studio). Uses Android's Accessibility Service.
website/   the download site (Next.js), deployed on Vercel
```

## Build the app

Open `android/` in Android Studio, let Gradle sync, then **Build › Build APK(s)**.
The APK lands in `android/app/build/outputs/apk/debug/`. Requires Android 8.0+ (API 26).

## Publish a release

1. Copy the APK to `website/public/ScrollStop-v<version>.apk`.
2. Add the version at the top of `releases` in `website/site.config.ts`.
3. Push. Vercel redeploys the site (set **Root Directory** to `website` in the Vercel project).

See `website/README.md` for the site settings (contact email, UPI).

## Why it isn't on the Play Store

Play doesn't allow apps to use Accessibility to stop you switching them off, and the 10-minute lock is
ScrollStop's strongest feature. It's a personal project, shared as an APK.
