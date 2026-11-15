package com.example.reelsblocker

import android.content.Context
import java.time.LocalDate

/**
 * A number that counts up during the day and resets to 0 at midnight
 * (the first time it is read on a new day), plus its daily limit.
 *
 * We use three of these:
 *  - reels opened full screen (from chats, posts, profiles)  -> [reels]
 *  - suggested posts/reels scrolled on the "For you" Home    -> [feedPosts]
 *  - YouTube Shorts opened full screen                       -> [shorts]
 *
 * The limit can be changed in the app (1..[MAX_LIMIT]).
 * Stored in SharedPreferences: a tiny private key-value file inside this app.
 */
class DailyCounter(context: Context, name: String) {

    companion object {
        const val DEFAULT_LIMIT = 30
        const val MIN_LIMIT = 1
        const val MAX_LIMIT = 50

        fun reels(context: Context) = DailyCounter(context, "reel_counter")
        fun feedPosts(context: Context) = DailyCounter(context, "feed_post_counter")
        fun shorts(context: Context) = DailyCounter(context, "shorts_counter")

        private const val KEY_DATE = "date"
        private const val KEY_COUNT = "count"
        private const val KEY_LIMIT = "limit"
    }

    private val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    /** Today's limit (read fresh every time, so changes apply immediately). */
    val limit: Int
        get() = prefs.getInt(KEY_LIMIT, DEFAULT_LIMIT).coerceIn(MIN_LIMIT, MAX_LIMIT)

    fun setLimit(value: Int) {
        prefs.edit().putInt(KEY_LIMIT, value.coerceIn(MIN_LIMIT, MAX_LIMIT)).apply()
    }

    /** How many today. */
    fun today(): Int {
        resetIfNewDay()
        return prefs.getInt(KEY_COUNT, 0)
    }

    fun limitReached(): Boolean = today() >= limit

    /** Adds one and returns the new total. */
    fun increment(): Int {
        val newCount = today() + 1
        prefs.edit().putInt(KEY_COUNT, newCount).apply()
        return newCount
    }

    private fun resetIfNewDay() {
        val todayStr = LocalDate.now().toString()        // e.g. "2026-11-15"
        if (prefs.getString(KEY_DATE, null) != todayStr) {
            prefs.edit().putString(KEY_DATE, todayStr).putInt(KEY_COUNT, 0).apply()
        }
    }
}
