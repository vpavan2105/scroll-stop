package com.example.reelsblocker

import android.content.Context

/** Things the app can (re)learn in teach mode, and which app each one lives in. */
enum class TeachTarget(val title: String, val key: String, val packageName: String) {
    REELS_TAB("Reels button", "reels_tab", InstagramIds.PACKAGE),
    EXPLORE_TAB("Explore button", "explore_tab", InstagramIds.PACKAGE),
    REELS_PLAYER("Reels player", "reels_player", InstagramIds.PACKAGE),
    SHORTS_TAB("Shorts button", "shorts_tab", YouTubeIds.PACKAGE),
    SHORTS_PLAYER("Shorts player", "shorts_player", YouTubeIds.PACKAGE);

    val isPlayer: Boolean get() = this == REELS_PLAYER || this == SHORTS_PLAYER
}

/**
 * Names learned in teach mode, saved on the phone. They are tried BEFORE the
 * built-in names in [InstagramIds] / [YouTubeIds], so after an app update you
 * can fix things yourself without rebuilding.
 *
 * A learned value is either a view ID ("com.instagram.android:id/xyz")
 * or, if the button had no ID, its label ("label:Reels").
 */
class LearnedIds(context: Context) {

    companion object {
        const val LABEL_PREFIX = "label:"
    }

    private val prefs = context.getSharedPreferences("learned_ids", Context.MODE_PRIVATE)

    fun get(target: TeachTarget): String? = prefs.getString(target.key, null)

    fun save(target: TeachTarget, value: String) {
        prefs.edit().putString(target.key, value).apply()
    }

    fun clear() = prefs.edit().clear().apply()

    /** View IDs to try: the learned one first, then the built-in ones. */
    fun ids(target: TeachTarget, builtIn: List<String>): List<String> {
        val v = get(target)
        return if (v != null && !v.startsWith(LABEL_PREFIX)) listOf(v) + builtIn else builtIn
    }

    /** Labels to try: the learned one first, then the built-in ones. */
    fun labels(target: TeachTarget, builtIn: List<String>): List<String> {
        val v = get(target)
        return if (v != null && v.startsWith(LABEL_PREFIX)) listOf(v.removePrefix(LABEL_PREFIX)) + builtIn
        else builtIn
    }

    /** For the settings screen. */
    fun describe(): String {
        val lines = TeachTarget.entries.mapNotNull { t ->
            get(t)?.let { "${t.title}: ${it.substringAfter(":id/")}" }
        }
        return if (lines.isEmpty()) "Nothing learned yet (using built-in names)."
        else lines.joinToString("\n")
    }
}
