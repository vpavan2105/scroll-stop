package com.example.reelsblocker

import android.os.Handler
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent

/**
 * Counts videos in a full-screen swipe player (Instagram Reels or YouTube
 * Shorts) and closes the player once today's limit is used up.
 *
 * One object per app, so the same logic serves both:
 *   val reels  = PlayerLimiter(DailyCounter.reels(..),  "reel",  "reels",  ...)
 *   val shorts = PlayerLimiter(DailyCounter.shorts(..), "Short", "Shorts", ...)
 *
 * How counting works:
 *  - the player opening = 1 video
 *  - each swipe = 1 more, decided once the scrolling has stopped
 *    (a swipe that snaps back to the same video doesn't count)
 */
class PlayerLimiter(
    private val counter: DailyCounter,
    private val singular: String,
    private val plural: String,
    private val handler: Handler,
    private val goBack: () -> Unit,
    private val toast: (String) -> Unit,
    private val onError: (String, Throwable) -> Unit,
) {
    companion object {
        private const val SWIPE_SETTLE_MS = 450L     // a swipe is "done" after this much quiet
        private const val OPEN_GRACE_MS = 700L       // ignore scroll noise right after opening
    }

    var isOpen = false
        private set
    private var openedAt = 0L
    private var lastIndex = -1
    private var countedIndex = Int.MIN_VALUE
    private var blocking = false

    /** Called after every screen check: is the player on screen right now? */
    fun update(playerVisible: Boolean) {
        when {
            playerVisible && !isOpen -> opened()
            !playerVisible && isOpen -> close()
            playerVisible && blocking -> goBack()     // Back didn't close it yet: try again
        }
    }

    fun close() {
        isOpen = false
        blocking = false
        handler.removeCallbacks(settled)
    }

    /** The player's pager scrolled: probably a swipe to the next video. */
    fun onScrolled(event: AccessibilityEvent) {
        val index = event.fromIndex                  // position of the visible video, -1 if unknown
        if (SystemClock.uptimeMillis() - openedAt < OPEN_GRACE_MS) {
            if (index >= 0) countedIndex = index     // remember where we started
            return
        }
        if (index >= 0) lastIndex = index
        handler.removeCallbacks(settled)             // wait until the scrolling stops
        handler.postDelayed(settled, SWIPE_SETTLE_MS)
    }

    private val settled = Runnable {
        try {
            if (!isOpen) return@Runnable
            val index = lastIndex
            if (index >= 0 && index == countedIndex) return@Runnable   // snapped back: same video
            if (index >= 0) countedIndex = index
            onNewVideo()
        } catch (t: Throwable) {
            onError("$plural swipe", t)
        }
    }

    private fun opened() {
        isOpen = true
        openedAt = SystemClock.uptimeMillis()
        lastIndex = -1
        countedIndex = Int.MIN_VALUE
        blocking = false
        onNewVideo()                                 // the first video counts
    }

    private fun onNewVideo() {
        if (counter.limitReached()) {
            blocking = true
            toast("Daily limit reached: ${counter.limit} $plural. See you tomorrow!")
            goBack()
            return
        }
        when (val left = counter.limit - counter.increment()) {
            10, 5 -> toast("$left $plural left today")
            1 -> toast("1 $singular left today")
            0 -> toast("That was your last $singular for today")
        }
    }
}
