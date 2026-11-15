package com.example.reelsblocker

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

/**
 * Is the blocker switched on, and is it actually running?
 * "Switched on but not running" usually means it crashed and Android stopped it.
 */
object ServiceStatus {

    /** Switched on in Settings > Accessibility. */
    fun isEnabled(context: Context): Boolean {
        val enabled = Settings.Secure.getString(
            context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val me = ComponentName(context, ReelsBlockerService::class.java)
        return enabled.split(':').any { ComponentName.unflattenFromString(it) == me }
    }

    /** Actually running right now (Android's own list of active services). */
    fun isRunning(context: Context): Boolean {
        val manager = context.getSystemService(AccessibilityManager::class.java)
        return manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any {
                it.resolveInfo.serviceInfo.packageName == context.packageName &&
                    it.resolveInfo.serviceInfo.name == ReelsBlockerService::class.java.name
            }
    }

    /** Show or clear the "ScrollStop is OFF" notification. */
    fun checkAndNotify(context: Context) {
        if (OffGuard.active(context) && !isRunning(context)) {
            // Switched off on purpose: the off-guard blocks Instagram/YouTube instead.
            Notifier.cancel(context, Notifier.ID_OFF)
            WatcherService.start(context)
            return
        }
        when {
            isRunning(context) -> Notifier.cancel(context, Notifier.ID_OFF)
            isEnabled(context) -> Notifier.showOff(
                context, "It stopped working (probably crashed). Tap, then turn it off and on again."
            )
            else -> Notifier.showOff(context, "Protection is switched off. Tap to turn it back on.")
        }
    }
}

/**
 * A small background job that Android runs about every 15 minutes, even
 * after a restart. It checks the blocker is still running — because a
 * crashed service can't tell you itself that it crashed.
 */
class WatchdogJobService : JobService() {

    companion object {
        private const val JOB_ID = 4201
        private const val EVERY_MS = 15 * 60 * 1000L     // Android's minimum for repeating jobs

        fun schedule(context: Context) {
            val scheduler = context.getSystemService(JobScheduler::class.java)
            if (scheduler.getPendingJob(JOB_ID) != null) return   // already scheduled
            val job = JobInfo.Builder(JOB_ID, ComponentName(context, WatchdogJobService::class.java))
                .setPeriodic(EVERY_MS)
                .setPersisted(true)                                // survive a phone restart
                .build()
            scheduler.schedule(job)
        }
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        ServiceStatus.checkAndNotify(this)
        return false            // finished; nothing left running in the background
    }

    override fun onStopJob(params: JobParameters?): Boolean = false
}
