package com.example.reelsblocker.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import com.example.reelsblocker.R

/** What the panel lets you do. */
enum class UnblockMode {
    /** Strict lock is on: you have to request an unlock in the app first. */
    LOCKED,
    /** Unlocked (or the Play Store version): you can continue after reading. */
    CAN_CONTINUE,
}

/**
 * Full-screen panel drawn over the Settings page that switches ScrollStop
 * off. It doesn't argue or shame; it reminds you who is really in control.
 *
 * Built with the service's Context, because it lives in an accessibility
 * overlay window on top of Settings, not inside one of our screens.
 */
fun Context.unblockMessagePanel(
    mode: UnblockMode,
    onKeep: () -> Unit,
    onContinue: () -> Unit,
    onOpenApp: () -> Unit,
): View = column().apply {
    gravity = Gravity.CENTER
    setBackgroundColor(Palette.INK)
    setPadding(dp(28), dp(32), dp(28), dp(32))

    addChild(
        iconTile(R.drawable.ic_shield_check, bg = Palette.AMBER_BG, fg = Palette.AMBER, iconDp = 30).apply {
            background = rounded(Palette.AMBER_BG, dp(20).toFloat())
        },
        dp(64), dp(64), gravity = Gravity.CENTER_HORIZONTAL,
    )
    addChild(
        text("This app isn't controlling you.", TextStyle.TITLE).apply { gravity = Gravity.CENTER },
        top = dp(22),
    )
    addChild(
        text("Social media is.", TextStyle.DISPLAY, Palette.AMBER).apply { gravity = Gravity.CENTER },
        top = dp(6),
    )
    val body = when (mode) {
        UnblockMode.LOCKED ->
            "You switched this on to take back your time. To switch it off, open ScrollStop, " +
                "request an unlock and wait 10 minutes."
        UnblockMode.CAN_CONTINUE ->
            "You set these limits for yourself. If you still want to switch protection off, that's your call."
    }
    addChild(
        text(body, TextStyle.BODY, Palette.TEXT_SOFT).apply { gravity = Gravity.CENTER },
        top = dp(16),
    )

    addChild(button("Keep protection on") { onKeep() }, top = dp(28))
    when (mode) {
        UnblockMode.LOCKED ->
            addChild(button("Open ScrollStop", ButtonKind.SECONDARY) { onOpenApp() }, top = dp(10))
        UnblockMode.CAN_CONTINUE ->
            addChild(button("Continue to switch off", ButtonKind.GHOST, 44) { onContinue() }, top = dp(8))
    }
}
