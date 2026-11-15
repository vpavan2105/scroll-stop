package com.example.reelsblocker

import android.content.Context
import android.os.SystemClock
import android.provider.Settings

/**
 * The "10 minutes to unlock" rule.
 *
 *  Locked ──(request unlock)──> Waiting 10 min ──> Unlocked for 5 min ──> Locked
 *
 * While not Unlocked you can't switch the blocker off in Accessibility
 * settings, or raise the daily limits. (Uninstalling is always allowed.)
 *
 * Time is measured with the phone's "time since boot" clock, so changing the
 * date/time in Settings doesn't skip the wait. A restart cancels a pending
 * request (you just request again).
 */
class ProtectionLock(private val context: Context) {

    companion object {
        const val WAIT_MS = 10 * 60 * 1000L
        const val OPEN_MS = 5 * 60 * 1000L
        private const val KEY_REQUESTED_AT = "requested_at"
        private const val KEY_BOOT = "boot_count"
    }

    sealed interface State {
        object Locked : State
        data class Waiting(val msLeft: Long) : State
        data class Unlocked(val msLeft: Long) : State
    }

    private val prefs = context.getSharedPreferences("protection_lock", Context.MODE_PRIVATE)

    fun state(): State {
        val requestedAt = prefs.getLong(KEY_REQUESTED_AT, -1L)
        if (requestedAt < 0) return State.Locked
        if (prefs.getInt(KEY_BOOT, -1) != bootCount()) { lockAgain(); return State.Locked }

        val elapsed = SystemClock.elapsedRealtime() - requestedAt
        return when {
            elapsed < WAIT_MS -> State.Waiting(WAIT_MS - elapsed)
            elapsed < WAIT_MS + OPEN_MS -> State.Unlocked(WAIT_MS + OPEN_MS - elapsed)
            else -> { lockAgain(); State.Locked }
        }
    }

    fun isUnlocked() = state() is State.Unlocked

    /** Start the 10-minute wait (does nothing if already waiting/unlocked). */
    fun requestUnlock() {
        if (state() != State.Locked) return
        prefs.edit()
            .putLong(KEY_REQUESTED_AT, SystemClock.elapsedRealtime())
            .putInt(KEY_BOOT, bootCount())
            .apply()
    }

    fun lockAgain() {
        prefs.edit().remove(KEY_REQUESTED_AT).remove(KEY_BOOT).apply()
    }

    private fun bootCount() =
        Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, 0)
}
