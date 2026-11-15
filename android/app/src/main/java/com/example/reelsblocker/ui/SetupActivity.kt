package com.example.reelsblocker.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import com.example.reelsblocker.Notifier
import com.example.reelsblocker.R
import com.example.reelsblocker.ServiceStatus

/**
 * Setup (design screen 1): explains what access the app needs BEFORE asking,
 * then walks through the three steps. Each step ticks itself off when done.
 */
class SetupActivity : Screen() {

    private data class Step(val title: String, val hint: String, val done: Boolean)

    private val prefs by lazy { getSharedPreferences("setup", MODE_PRIVATE) }

    override fun build() {
        val steps = steps()
        val current = steps.indexOfFirst { !it.done }          // -1 = everything done

        // Progress
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
                "Android calls this an Accessibility service. It lets the app see Instagram's screen " +
                    "so it can hide the Reels and Explore buttons.",
                TextStyle.BODY, Palette.MUTED,
            ),
            8,
        )

        place(whatThisAllows(), 22)
        place(stepList(steps, current), 14)

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
        // Android 13+ hides "Allow restricted settings" until you've tried to
        // turn the service on once and seen the "Restricted setting" message.
        val tried = prefs.getBoolean("restricted_tried", false)
        return listOf(
            Step(
                "Allow restricted settings",
                when {
                    !restrictedNeeded -> "Not needed on this phone"
                    !tried -> "Android blocks this for apps installed from a file. First tap ScrollStop in " +
                        "Accessibility. It will say \"Restricted setting\". Close that message and come back."
                    else -> "Now open App info, tap ⋮ (top right) › Allow restricted settings, confirm, then come back."
                },
                !restrictedNeeded || enabled || prefs.getBoolean("restricted_done", false),
            ),
            Step("Turn on in Accessibility", "Settings › Accessibility › ScrollStop", ServiceStatus.isRunning(this)),
            Step("Allow notifications", "So you hear about it if protection stops", Notifier.allowed(this)),
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
        0 -> if (!prefs.getBoolean("restricted_tried", false)) {
            button("Open Accessibility settings") {
                prefs.edit().putBoolean("restricted_tried", true).apply()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        } else {
            button("Open App info") {
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)))
            }
        }
        1 -> button("Open Accessibility settings") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        2 -> button("Allow notifications") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            } else {
                startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName))
            }
        }
        else -> button("Done") { finish() }
    }

    private fun secondaryAction(current: Int): View? = when (current) {
        0 -> button(
            if (prefs.getBoolean("restricted_tried", false)) "I've allowed it" else "I didn't see that message",
            ButtonKind.GHOST, 44,
        ) {
            prefs.edit().putBoolean("restricted_done", true).apply()
            rebuild()
        }
        1 -> button("I've turned it on", ButtonKind.GHOST, 44) { rebuild() }
        2 -> button("Skip for now", ButtonKind.GHOST, 44) { finish() }
        else -> null
    }
}
