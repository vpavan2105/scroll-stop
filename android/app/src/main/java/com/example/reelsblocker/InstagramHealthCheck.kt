package com.example.reelsblocker

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.SystemClock
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Layer 2: notices when an Instagram update broke the blocker.
 *
 * We can recognise Instagram's bottom bar by its SHAPE alone (a row of 4–6
 * tappable buttons across the bottom), without knowing any names. If the bar
 * is there but we can't find the Reels or Explore button in it, ~10 checks in
 * a row (about 20 s), something changed. Then we:
 *   1. save a screen capture automatically
 *   2. show a notification (once per Instagram version)
 *   3. remember which Instagram version last worked
 */
class InstagramHealthCheck(private val service: AccessibilityService) {

    companion object {
        private const val CHECK_EVERY_MS = 2_000L
        private const val MISSES_BEFORE_WARNING = 10
        private const val KEY_LAST_WORKING = "last_working_version"
        private const val KEY_WARNED = "warned_version"
        private const val KEY_CHECKED = "checked_version"
        private const val KEY_REELS_FOUND = "reels_found"
        private const val KEY_EXPLORE_FOUND = "explore_found"

        fun prefs(context: Context) =
            context.getSharedPreferences("instagram_health", Context.MODE_PRIVATE)

        fun instagramVersion(context: Context): String = runCatching {
            context.packageManager.getPackageInfo(InstagramIds.PACKAGE, 0).versionName
        }.getOrNull() ?: "unknown"

        fun lastWorkingVersion(context: Context): String? =
            prefs(context).getString(KEY_LAST_WORKING, null)

        /** What the last check saw, for the settings screens. */
        data class Snapshot(
            val instagramVersion: String,
            val lastWorkingVersion: String?,
            val checked: Boolean,          // checked at least once with this Instagram version
            val reelsFound: Boolean,
            val exploreFound: Boolean,
        ) {
            /** True once we warned you about this version and a button is still missing. */
            val problem: Boolean get() = checked && (!reelsFound || !exploreFound)
        }

        fun snapshot(context: Context): Snapshot {
            val p = prefs(context)
            val version = instagramVersion(context)
            val checked = p.getString(KEY_CHECKED, null) == version
            val reels = p.getBoolean(KEY_REELS_FOUND, true)
            val explore = p.getBoolean(KEY_EXPLORE_FOUND, true)
            val warned = p.getString(KEY_WARNED, null) == version
            return Snapshot(
                instagramVersion = version,
                lastWorkingVersion = p.getString(KEY_LAST_WORKING, null),
                checked = checked,
                reelsFound = reels || !warned,      // only report "missing" after the warning fired
                exploreFound = explore || !warned,
            )
        }
    }

    private var lastSaved: Triple<String, Boolean, Boolean>? = null

    private var lastCheckAt = 0L
    private var misses = 0

    fun onInstagramScreen(root: AccessibilityNodeInfo, reelsTabFound: Boolean, exploreTabFound: Boolean) {
        val now = SystemClock.uptimeMillis()
        if (now - lastCheckAt < CHECK_EVERY_MS) return
        lastCheckAt = now

        if (!bottomBarVisible(root)) return          // e.g. inside a chat: can't judge here

        val version = instagramVersion(service)
        val prefs = prefs(service)

        // Remember what we saw (only when it changes, to avoid writing every 2 s)
        val seen = Triple(version, reelsTabFound, exploreTabFound)
        if (seen != lastSaved) {
            lastSaved = seen
            prefs.edit().putString(KEY_CHECKED, version)
                .putBoolean(KEY_REELS_FOUND, reelsTabFound)
                .putBoolean(KEY_EXPLORE_FOUND, exploreTabFound)
                .apply()
        }

        if (reelsTabFound && exploreTabFound) {
            misses = 0
            prefs.edit().putString(KEY_LAST_WORKING, version).apply()
            Notifier.cancel(service, Notifier.ID_INSTAGRAM_CHANGED)
            return
        }

        misses++
        if (misses < MISSES_BEFORE_WARNING) return
        misses = 0
        if (prefs.getString(KEY_WARNED, null) == version) return   // already told you
        prefs.edit().putString(KEY_WARNED, version).apply()

        val roots = service.windows.mapNotNull { it.root }.ifEmpty { listOf(root) }
        ScreenDumper.dump(service, roots)

        val missing = listOfNotNull(
            "Reels".takeIf { !reelsTabFound },
            "Explore".takeIf { !exploreTabFound },
        ).joinToString(" and ")
        val lastOk = prefs.getString(KEY_LAST_WORKING, null)?.let { " Last worked with $it." } ?: ""
        Notifier.showInstagramChanged(
            service,
            "Can't find the $missing button (Instagram $version).$lastOk " +
                "A screen capture was saved. Open ScrollStop and use \"Re-learn\" to fix it.",
        )
    }

    /** A row of 4–6 tappable, similar-sized buttons spread across the bottom of the screen. */
    private fun bottomBarVisible(root: AccessibilityNodeInfo): Boolean {
        val metrics = service.resources.displayMetrics
        val h = metrics.heightPixels
        val w = metrics.widthPixels
        val candidates = mutableListOf<android.graphics.Rect>()
        NodeSearch.forEach(root) { n ->
            if (n.isClickable && n.isVisibleToUser) {
                val b = NodeSearch.boundsOf(n)
                if (b.top >= h * 0.82 && b.height() in (h * 0.03).toInt()..(h * 0.13).toInt() &&
                    b.width() in (w / 8)..(w / 3)
                ) candidates.add(b)
            }
        }
        // group buttons that sit on the same line
        return candidates.groupBy { it.centerY() / 40 }.values.any { row ->
            row.size in 4..6 && (row.maxOf { it.right } - row.minOf { it.left }) >= w * 0.8
        }
    }
}
