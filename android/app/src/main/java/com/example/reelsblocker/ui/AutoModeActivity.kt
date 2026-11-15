package com.example.reelsblocker.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import com.example.reelsblocker.OffGuard

/**
 * "Restricted apps" screen. Explains how to use an app that is blocked while ScrollStop is on
 * to the Accessibility page to switch ScrollStop off. The two permissions it
 * relies on are set up once in the main setup and can't be turned off here.
 */
class AutoModeActivity : Screen() {

    override fun build() {
        val usage = OffGuard.hasUsageAccess(this)
        val overlay = OffGuard.hasOverlay(this)
        val ready = usage && overlay

        placeBackHeader("Restricted apps")

        place(
            text(
                "Some apps (like banking apps such as YONO) won't open while ScrollStop is switched on. " +
                    "You can switch ScrollStop off any time to bank — and while it's off, " +
                    "Instagram and YouTube stay blocked until you switch it back on.",
                TextStyle.BODY, Palette.MUTED,
            ),
            12,
        )

        if (ready) {
            place(chip("Ready · Instagram & YouTube blocked while off", Palette.MINT, Palette.MINT_BG), 18)

            place(sectionLabel("To bank"), 22)
            place(
                listCard(
                    listOf(
                        howRow("1", "Switch ScrollStop off in Accessibility (the button below)."),
                        howRow("2", "Open your banking app and do what you need."),
                        howRow("3", "Switch ScrollStop back on. The notification takes you there."),
                    )
                ),
                10,
            )

            placeSpacer()
            place(button("Switch off for banking") {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }, 20)
            place(
                text(
                    "Instagram and YouTube stay blocked the whole time it's off.",
                    TextStyle.CAPTION, Palette.FAINT,
                ),
                10,
            )
        } else {
            place(
                warningBanner(
                    "Finish setup first",
                    "Banking mode needs two permissions. Allow the ones marked below, then come back.",
                ),
                16,
            )
            place(sectionLabel("Still needed"), 22)
            if (!usage) {
                place(permStep("Usage access", "Lets ScrollStop notice when Instagram or YouTube opens.") {
                    try {
                        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.fromParts("package", packageName, null)))
                    } catch (_: Exception) {
                        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    }
                }, 10)
            }
            if (!overlay) {
                place(permStep("Display over other apps", "Lets ScrollStop show the block screen over Instagram and YouTube.") {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", packageName, null)))
                }, 10)
            }
        }
    }

    private fun howRow(number: String, line: String): View {
        val r = row().apply { setPadding(dp(16), dp(14), dp(16), dp(14)) }
        r.addChild(text(number, TextStyle.HEADING, Palette.AMBER), dp(22), WRAP)
        r.addChild(text(line, TextStyle.SMALL, Palette.TEXT_SOFT), 0, WRAP, weight = 1f, start = dp(10))
        return r
    }

    private fun permStep(title: String, body: String, onClick: () -> Unit): View {
        val card = card()
        card.addChild(text(title, TextStyle.HEADING))
        card.addChild(text(body, TextStyle.SMALL, Palette.MUTED), top = dp(4))
        card.addChild(button("Allow", ButtonKind.OUTLINE, 44) { onClick() }, top = dp(14))
        return card
    }
}
