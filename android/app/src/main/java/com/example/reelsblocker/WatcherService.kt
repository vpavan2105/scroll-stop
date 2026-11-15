package com.example.reelsblocker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import com.example.reelsblocker.ui.BlockActivity

/**
 * Runs ONLY while ScrollStop's accessibility service is switched OFF (the
 * off-guard, see [OffGuard]). About once a second, while the screen is on, it
 * checks which app is in front. If Instagram or YouTube is open it drops you out
 * with the block screen ([BlockActivity]) and keeps a reminder to turn ScrollStop
 * back on. As soon as the accessibility service is on again, it stops itself.
 */
class WatcherService : Service() {

    companion object {
        private const val CHANNEL_ID = "offguard"
        private const val NOTIFICATION_ID = 10
        private const val TICK_MS = 700L
        private const val SCREEN_OFF_TICK_MS = 3_000L
        private const val BLOCK_COOLDOWN_MS = 1_500L

        fun start(context: Context) {
            if (!OffGuard.active(context)) return
            if (ServiceStatus.isRunning(context)) return      // accessibility on: not needed
            try {
                context.startForegroundService(Intent(context, WatcherService::class.java))
            } catch (t: Throwable) {
                ErrorLog.record(context, "guard start: ${t.javaClass.simpleName} ${t.message}")
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, WatcherService::class.java))
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var foreground: String? = null
    private var lastQueryAt = 0L
    private var lastBlockAt = 0L

    private val tick = object : Runnable {
        override fun run() {
            try {
                check()
            } catch (t: Throwable) {
                ErrorLog.record(this@WatcherService, "guard: ${t.javaClass.simpleName} ${t.message}")
            }
            val interactive = getSystemService(PowerManager::class.java).isInteractive
            handler.postDelayed(this, if (interactive) TICK_MS else SCREEN_OFF_TICK_MS)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /** Android requires startForeground() after each startForegroundService(). */
    private fun goForeground() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        goForeground()
        if (!OffGuard.active(this) || ServiceStatus.isRunning(this)) {
            stopSelf()
            return START_NOT_STICKY
        }
        handler.removeCallbacks(tick)
        handler.post(tick)
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(tick)
        super.onDestroy()
    }

    private fun check() {
        // Accessibility back on, guard switched off, or a permission removed: done.
        if (!OffGuard.active(this) || ServiceStatus.isRunning(this)) {
            stopSelf()
            return
        }
        if (!getSystemService(PowerManager::class.java).isInteractive) return

        val pkg = currentForegroundApp() ?: return
        if (pkg in OffGuard.BLOCKED_APPS) {
            val now = SystemClock.uptimeMillis()
            if (now - lastBlockAt < BLOCK_COOLDOWN_MS) return
            lastBlockAt = now
            blockNow()
        }
    }

    /** Bring our block screen to the front, pushing Instagram/YouTube to the background. */
    private fun blockNow() {
        try {
            startActivity(
                Intent(this, BlockActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        } catch (t: Throwable) {
            ErrorLog.record(this, "block: ${t.javaClass.simpleName} ${t.message}")
        }
    }

    /** The app most recently brought to the front, from Android's usage events. */
    private fun currentForegroundApp(): String? {
        val usage = getSystemService(UsageStatsManager::class.java)
        val now = System.currentTimeMillis()
        val from = if (lastQueryAt == 0L) now - 5 * 60_000 else lastQueryAt - 1_000
        lastQueryAt = now
        val events = usage.queryEvents(from, now)
        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            @Suppress("DEPRECATION")
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) foreground = event.packageName
        }
        return foreground
    }

    private fun buildNotification(): Notification {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Switched off", NotificationManager.IMPORTANCE_LOW)
        )
        val open = PendingIntent.getActivity(
            this, NOTIFICATION_ID,
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("ScrollStop is off")
            .setContentText("Instagram and YouTube are blocked. Tap to turn ScrollStop back on.")
            .setStyle(Notification.BigTextStyle().bigText("Instagram and YouTube stay blocked while ScrollStop is off. Tap to turn it back on."))
            .setContentIntent(open)
            .setOngoing(true)
            .build()
    }
}

/** Restarts the off-guard watcher after the phone restarts (if it's set up and ScrollStop is off). */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) WatcherService.start(context)
    }
}
