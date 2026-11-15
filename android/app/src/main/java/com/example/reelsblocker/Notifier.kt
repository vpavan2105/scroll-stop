package com.example.reelsblocker

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.example.reelsblocker.ui.MainActivity

/**
 * Shows the app's notifications. Three kinds, each with its own ID so a new
 * one replaces the old one of the same kind instead of piling up.
 */
object Notifier {

    private const val CHANNEL_ID = "status"
    const val ID_OFF = 1                 // the blocker is not running
    const val ID_INSTAGRAM_CHANGED = 2   // can't find Instagram's buttons any more
    const val ID_ERRORS = 3              // many errors in a short time

    /** "ScrollStop is OFF" — tapping opens Accessibility settings. */
    fun showOff(context: Context, text: String) = show(
        context, ID_OFF, "ScrollStop is OFF", text,
        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),
    )

    fun showInstagramChanged(context: Context, text: String) = show(
        context, ID_INSTAGRAM_CHANGED, "Instagram changed", text,
        Intent(context, MainActivity::class.java),
    )

    fun showErrors(context: Context, text: String) = show(
        context, ID_ERRORS, "ScrollStop hit errors", text,
        Intent(context, MainActivity::class.java),
    )

    fun cancel(context: Context, id: Int) {
        context.getSystemService(NotificationManager::class.java).cancel(id)
    }

    /** Android 13+ asks the user before an app may show notifications. */
    fun allowed(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    private fun show(context: Context, id: Int, title: String, text: String, tapIntent: Intent) {
        if (!allowed(context)) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Blocker status", NotificationManager.IMPORTANCE_DEFAULT)
        )
        val pending = PendingIntent.getActivity(
            context, id,
            tapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(Notification.BigTextStyle().bigText(text))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        manager.notify(id, notification)
    }
}
