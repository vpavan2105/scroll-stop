package com.example.reelsblocker

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.provider.Settings

/**
 * "Off-guard". You can switch ScrollStop's accessibility service off at any time
 * (for example so a banking app like YONO opens — banks refuse to run while an
 * unknown accessibility service is on). To stop that becoming a free pass to
 * Reels, while the service is OFF a light watcher ([WatcherService]) blocks
 * Instagram and YouTube and reminds you to switch ScrollStop back on.
 *
 * Needs two one-tap permissions, no computer:
 *  - Usage access, to notice which app is in front
 *  - Display over other apps, to show the block screen over Instagram/YouTube
 */
object OffGuard {

    /** Apps blocked while ScrollStop is switched off. */
    val BLOCKED_APPS = setOf(InstagramIds.PACKAGE, YouTubeIds.PACKAGE)

    fun hasUsageAccess(context: Context): Boolean {
        val ops = context.getSystemService(AppOpsManager::class.java)
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ops.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        } else {
            @Suppress("DEPRECATION")
            ops.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun hasOverlay(context: Context): Boolean = Settings.canDrawOverlays(context)

    /** Both permissions present, so the guard can actually work. */
    fun ready(context: Context): Boolean = hasUsageAccess(context) && hasOverlay(context)

    /**
     * The off-guard runs whenever both required permissions are present. There is
     * no separate on/off switch: the permissions are required at setup, so turning
     * ScrollStop off always leaves Instagram and YouTube blocked.
     */
    fun active(context: Context): Boolean = ready(context)
}
