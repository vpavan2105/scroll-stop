package com.example.reelsblocker.ui

import android.content.Intent
import android.provider.Settings
import android.view.Gravity
import com.example.reelsblocker.R
import com.example.reelsblocker.ServiceStatus

/**
 * Shown over Instagram or YouTube while ScrollStop is switched off. It drops you
 * out of the app and offers to turn ScrollStop back on. Pressing Back goes to the
 * home screen, not back into Instagram.
 */
class BlockActivity : Screen() {

    override val tickEverySecond = true

    override fun build() {
        // Protection already back on? Nothing to block.
        if (ServiceStatus.isRunning(this)) { finish(); return }

        placeSpacer()
        val badge = row().apply { gravity = Gravity.CENTER }
        badge.addChild(iconTile(R.drawable.ic_reels_off, sizeDp = 56, iconDp = 26), dp(56), dp(56))
        place(badge, 0)
        place(text("ScrollStop is off", TextStyle.DISPLAY).apply { gravity = Gravity.CENTER }, 16)
        place(
            text(
                "Instagram and YouTube stay blocked while ScrollStop is switched off. " +
                    "Turn it back on to use them.",
                TextStyle.BODY, Palette.MUTED,
            ).apply { gravity = Gravity.CENTER },
            12,
        )
        placeSpacer()
        place(button("Turn ScrollStop on") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }, 20)
        place(button("Go to home screen", ButtonKind.GHOST, 48) { goHome() }, 8)
    }

    /** Close this screen when protection comes back on. */
    override fun tick() {
        if (ServiceStatus.isRunning(this)) finish()
    }

    private fun goHome() {
        startActivity(
            Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
    }

    @Deprecated("Back goes home, not back into Instagram")
    override fun onBackPressed() {
        goHome()
    }
}
