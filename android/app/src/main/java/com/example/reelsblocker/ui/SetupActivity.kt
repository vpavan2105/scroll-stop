package com.example.reelsblocker.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.example.reelsblocker.Notifier
import com.example.reelsblocker.OffGuard
import com.example.reelsblocker.R
import com.example.reelsblocker.ServiceStatus

/**
 * Setup (design screen 1). Order: allow restricted settings first (some phones
 * gate Usage access and Display-over-other-apps behind it too), then those two
 * permissions, then notifications, and finally turn on in Accessibility.
 * Each step has an illustrated "how to" card.
 */
class SetupActivity : Screen() {

    private data class Step(val title: String, val hint: String, val done: Boolean)

    private val prefs by lazy { getSharedPreferences("setup", MODE_PRIVATE) }

    override fun build() {
        val steps = steps()
        val current = steps.indexOfFirst { !it.done }          // -1 = everything done

        place(text(if (current < 0) "All set" else "Step ${current + 1} of ${steps.size}", TextStyle.LABEL, Palette.FAINT), 0)
        val bar = row()
        steps.forEachIndexed { i, step ->
            val color = when {
                step.done -> Palette.MINT
                i == current -> Palette.AMBER
                else -> Palette.TRACK
            }
            bar.addChild(View(this).apply { background = rounded(color, dp(4).toFloat()) }, 0, dp(4), weight = 1f, start = if (i > 0) dp(6) else 0)
        }
        place(bar, 10)

        place(text(if (current < 0) "You're protected" else "Turn on ScrollStop", TextStyle.HERO), 22)
        place(
            text(
                "A few quick permissions. Android gates some of them for apps installed from a file, " +
                    "so allow restricted settings first — then the rest just work.",
                TextStyle.BODY, Palette.MUTED,
            ),
            8,
        )

        if (current <= 0) place(whatThisAllows(), 22)
        place(stepList(steps, current), 14)
        guideCard(current)?.let { place(it, 14) }

        placeSpacer()
        primaryAction(current)?.let { place(it, 20) }
        secondaryAction(current)?.let { place(it, 8) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        rebuild()
    }

    // ---------------------------------------------------------------- steps

    private fun steps(): List<Step> {
        val restrictedNeeded = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        val enabled = ServiceStatus.isEnabled(this)
        return listOf(
            Step(
                "Allow restricted settings",
                if (!restrictedNeeded) "Not needed on this phone"
                else "First tap ScrollStop in Accessibility › Installed apps, then allow it in App info. This unlocks the next steps.",
                !restrictedNeeded || enabled || prefs.getBoolean("restricted_done", false),
            ),
            Step(
                "Allow Usage access",
                "So ScrollStop can keep Instagram and YouTube blocked when it's switched off for banking.",
                OffGuard.hasUsageAccess(this),
            ),
            Step(
                "Allow Display over other apps",
                "So ScrollStop can show the block screen over Instagram and YouTube.",
                OffGuard.hasOverlay(this),
            ),
            Step(
                "Allow notifications",
                "So you hear about it if protection stops",
                Notifier.allowed(this) || prefs.getBoolean("notif_skipped", false),
            ),
            Step("Turn on in Accessibility", "Accessibility › Installed apps › ScrollStop › turn on → Allow.", ServiceStatus.isRunning(this)),
        )
    }

    private fun whatThisAllows(): View = card().apply {
        addChild(text("What this allows", TextStyle.HEADING))
        listOf(
            R.drawable.ic_eye to "It only sees Instagram, YouTube and Settings. Banking and all other apps are invisible to it.",
            R.drawable.ic_note to "It saves only today's counts and your limits.",
            R.drawable.ic_wifi_off to "No internet access, so nothing can leave your phone.",
        ).forEach { (iconRes, line) ->
            val r = row().apply { gravity = Gravity.TOP }
            r.addChild(icon(iconRes, Palette.MINT), dp(20), dp(20))
            r.addChild(text(line, TextStyle.SMALL, Palette.TEXT_SOFT), 0, WRAP, weight = 1f, start = dp(12))
            addChild(r, top = dp(14))
        }
    }

    private fun stepList(steps: List<Step>, current: Int): View = column().apply {
        steps.forEachIndexed { i, step ->
            val state = when {
                step.done -> StepState.DONE
                i == current -> StepState.CURRENT
                else -> StepState.LATER
            }
            val r = row().apply { setPadding(dp(4), dp(12), dp(4), dp(12)) }
            r.addChild(stepDot(state, i + 1, 28), dp(28), dp(28))
            val texts = column()
            texts.addChild(
                text(step.title, if (state == StepState.CURRENT) TextStyle.HEADING else TextStyle.BODY_MEDIUM,
                    if (state == StepState.CURRENT) Palette.TEXT else Palette.MUTED),
            )
            texts.addChild(
                text(if (state == StepState.DONE) "Done" else step.hint, TextStyle.CAPTION,
                    if (state == StepState.CURRENT) Palette.MUTED else Palette.FAINT),
                top = dp(2),
            )
            r.addChild(texts, 0, WRAP, weight = 1f, start = dp(14))
            addChild(r)
        }
    }

    // ---------------------------------------------------------------- buttons

    private fun primaryAction(current: Int): View? = when (current) {
        0 -> button("I've allowed restricted settings") {
            prefs.edit().putBoolean("restricted_done", true).apply()
            rebuild()
        }
        1 -> button("Allow Usage access") {
            try {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.fromParts("package", packageName, null)))
            } catch (_: Exception) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }
        2 -> button("Allow Display over other apps") {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", packageName, null)))
        }
        3 -> button("Allow notifications") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            } else {
                startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName))
            }
        }
        4 -> button("Open Accessibility settings") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        else -> button("Done") { finish() }
    }

    private fun secondaryAction(current: Int): View? = when (current) {
        0 -> button("My phone didn't block anything", ButtonKind.GHOST, 44) {
            prefs.edit().putBoolean("restricted_done", true).apply()
            rebuild()
        }
        3 -> button("Skip for now", ButtonKind.GHOST, 44) {
            prefs.edit().putBoolean("notif_skipped", true).apply()
            rebuild()
        }
        4 -> button("I've turned it on", ButtonKind.GHOST, 44) { rebuild() }
        else -> null
    }

    // ---------------------------------------------------------------- illustrated guide

    private fun guideCard(current: Int): View? {
        if (current == 0) return restrictedGuide()
        val title: String
        val lines: List<String>
        val showMock: Boolean
        when (current) {
            1 -> {
                title = "How to allow Usage access"
                lines = listOf(
                    "The \"Usage access\" list opens.",
                    "Scroll to ScrollStop and tap it.",
                    "Turn \"Permit usage access\" on.",
                )
                showMock = true
            }
            2 -> {
                title = "How to allow Display over other apps"
                lines = listOf(
                    "ScrollStop's page opens.",
                    "Turn \"Allow display over other apps\" on.",
                )
                showMock = true
            }
            3 -> {
                title = "Notifications"
                lines = listOf(
                    "Tap Allow when Android asks.",
                    "Used only to warn you if protection ever stops.",
                )
                showMock = false
            }
            4 -> {
                title = "How to turn it on in Accessibility"
                lines = listOf(
                    "Tap \"Open Accessibility settings\" below.",
                    "Open \"Installed apps\" (some phones say \"Downloaded apps\").",
                    "Tap ScrollStop, then turn the big switch on.",
                    "A dialog asks to allow full control — tap Allow.",
                    "Switch greyed out? Go back and finish \"Allow restricted settings\" first.",
                )
                showMock = true
            }
            else -> return null
        }

        val card = card()
        card.addChild(text(title, TextStyle.HEADING))
        lines.forEachIndexed { i, line ->
            val r = row().apply { gravity = Gravity.TOP }
            r.addChild(text((i + 1).toString(), TextStyle.SMALL_BOLD, Palette.AMBER), dp(18), WRAP)
            r.addChild(text(line, TextStyle.SMALL, Palette.TEXT_SOFT), 0, WRAP, weight = 1f, start = dp(8))
            card.addChild(r, top = dp(10))
        }
        if (showMock) card.addChild(toggleMock(), top = dp(14))
        return card
    }

    /** The restricted-settings step: both actions in order, because the Allow option only
     *  appears in App info AFTER you've tapped ScrollStop in Accessibility › Installed apps. */
    private fun restrictedGuide(): View {
        val card = card()
        card.addChild(text("Allow restricted settings — 2 steps", TextStyle.HEADING))
        listOf(
            "1.  Open Accessibility below, then \"Installed apps\" (some phones say \"Downloaded apps\").",
            "     Tap ScrollStop. Android shows \"Restricted setting\" — tap OK and come back.",
            "2.  Open App info below, tap the ⋮ menu (top-right), then \"Allow restricted settings\" and confirm.",
        ).forEach { line ->
            card.addChild(text(line, TextStyle.SMALL, Palette.TEXT_SOFT), top = dp(8))
        }
        card.addChild(button("Open Accessibility", ButtonKind.OUTLINE, 44) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }, top = dp(14))
        card.addChild(button("Open App info", ButtonKind.OUTLINE, 44) {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)))
        }, top = dp(8))
        card.addChild(
            text("The \"Allow restricted settings\" option only appears after step 1.", TextStyle.CAPTION, Palette.FAINT),
            top = dp(10),
        )
        return card
    }

    private fun toggleMock(): View {
        val rowv = row().apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = rounded(Palette.SUNKEN, dp(12).toFloat(), Palette.BORDER, dp(1))
        }
        rowv.addChild(
            iconTile(R.drawable.ic_launcher_foreground, bg = Palette.AMBER_BG, fg = Palette.AMBER, sizeDp = 30, iconDp = 18),
            dp(30), dp(30),
        )
        val texts = column()
        texts.addChild(text("ScrollStop", TextStyle.SMALL_BOLD))
        texts.addChild(text("turn this on", TextStyle.CAPTION, Palette.FAINT), top = dp(1))
        rowv.addChild(texts, 0, WRAP, weight = 1f, start = dp(10))

        val track = FrameLayout(this).apply { background = rounded(Palette.MINT, dp(13).toFloat()) }
        track.addView(
            View(this).apply { background = circle(0xFFFFFFFF.toInt()) },
            FrameLayout.LayoutParams(dp(18), dp(18)).apply {
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
                marginEnd = dp(4)
            },
        )
        rowv.addChild(track, dp(42), dp(26), start = dp(10))
        return rowv
    }
}
