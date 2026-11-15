package com.example.reelsblocker.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import com.example.reelsblocker.R

/**
 * The panel drawn over Instagram's suggested feed once today's limit is used up
 * (design screen 6). The whole panel is one tap target: tapping anywhere,
 * including the amber "button", opens the Following feed.
 *
 * Built with the service's Context, because it lives in an accessibility
 * overlay window, not inside one of our screens.
 */
fun Context.limitReachedPanel(limit: Int): View = column().apply {
    gravity = Gravity.CENTER
    setBackgroundColor(Palette.INK)
    setPadding(dp(28), dp(24), dp(28), dp(24))
    contentDescription = "Today's limit of $limit suggested posts is used up. Tap to show posts from people you follow."

    addChild(
        iconTile(R.drawable.ic_moon, bg = Palette.AMBER_BG, fg = Palette.AMBER, iconDp = 30).apply {
            background = rounded(Palette.AMBER_BG, dp(20).toFloat())
        },
        dp(64), dp(64), gravity = Gravity.CENTER_HORIZONTAL,
    )
    addChild(
        text("That's $limit suggested posts for today", TextStyle.TITLE).apply { gravity = Gravity.CENTER },
        top = dp(18),
    )
    addChild(
        text("Posts from people you follow are still here.", TextStyle.BODY, Palette.MUTED).apply { gravity = Gravity.CENTER },
        top = dp(8),
    )
    // Looks like a button; the whole panel handles the tap (see TabCover).
    addChild(
        text("Show Following", TextStyle.HEADING, Palette.ON_AMBER).apply {
            typeface = font(R.font.inter_bold)
            gravity = Gravity.CENTER
            background = rounded(Palette.AMBER, dp(16).toFloat())
            setPadding(dp(28), 0, dp(28), 0)
            minHeight = dp(52)
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        },
        WRAP, WRAP, top = dp(20), gravity = Gravity.CENTER_HORIZONTAL,
    )
    addChild(
        text("Resets at midnight", TextStyle.CAPTION, Palette.FAINT).apply { gravity = Gravity.CENTER },
        top = dp(14),
    )
}
