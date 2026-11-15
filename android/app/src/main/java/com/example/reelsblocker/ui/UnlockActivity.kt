package com.example.reelsblocker.ui

import android.content.Intent
import android.provider.Settings
import android.view.Gravity
import android.view.View
import com.example.reelsblocker.ProtectionLock

/**
 * Unlock (design screen 4). Shows the strict lock as a countdown ring:
 *   Locked → (Start) → Waiting 10 min → Unlocked 5 min → Locked again.
 * Calm wording on purpose: no guilt, just the facts and the time left.
 */
class UnlockActivity : Screen() {

    override val tickEverySecond = true

    private lateinit var lock: ProtectionLock
    private var ring: RingView? = null
    private var builtKind: Class<*>? = null

    override fun build() {
        lock = ProtectionLock(this)
        val state = lock.state()
        builtKind = state.javaClass

        placeBackHeader("Unlock")

        // The reminder, before anything else (not shown once you're already unlocked)
        if (state !is ProtectionLock.State.Unlocked) {
            val reminder = column().apply {
                gravity = Gravity.CENTER_HORIZONTAL
                background = rounded(Palette.AMBER_BG, dp(20).toFloat(), Palette.AMBER_BORDER, dp(1))
                setPadding(dp(20), dp(18), dp(20), dp(18))
                addChild(text("This app isn't controlling you.", TextStyle.HEADING, Palette.AMBER_BODY).apply { gravity = Gravity.CENTER })
                addChild(text("Social media is.", TextStyle.TITLE, Palette.AMBER).apply { gravity = Gravity.CENTER }, top = dp(4))
            }
            place(reminder, 18)
        }

        val ringView = RingView(this, sizeDp = 220, strokeDp = 12).apply {
            trackColor = Palette.SURFACE_2
            bigTextSp = 54f
        }
        ring = ringView
        showRing(state)
        place(column().apply {
            gravity = Gravity.CENTER_HORIZONTAL
            addChild(ringView, WRAP, WRAP, gravity = Gravity.CENTER_HORIZONTAL)
        }, 22)

        val message = when (state) {
            ProtectionLock.State.Locked ->
                "Switching protection off or raising a limit needs a short wait. Start it now and come back in 10 minutes."
            is ProtectionLock.State.Waiting ->
                "Take a breather. If you still want to switch protection off when the timer ends, you can."
            is ProtectionLock.State.Unlocked ->
                "Unlocked. You can switch protection off in Accessibility settings, or raise your limits. It locks again by itself."
        }
        place(text(message, TextStyle.BODY, Palette.TEXT_SOFT).apply {
            gravity = Gravity.CENTER
            setPadding(dp(20), 0, dp(20), 0)
        }, 16)

        place(stepsCard(state), 22)

        placeSpacer()
        when (state) {
            ProtectionLock.State.Locked -> {
                place(button("Start the 10-minute wait") { lock.requestUnlock(); rebuild() }, 20)
                place(note("Uninstalling is always allowed, with no wait."), 10)
            }
            is ProtectionLock.State.Waiting -> {
                place(button("Cancel request", ButtonKind.SECONDARY) { lock.lockAgain(); rebuild() }, 20)
                place(note("You can leave this screen. Restarting the phone cancels the request."), 10)
            }
            is ProtectionLock.State.Unlocked -> {
                place(button("Open Accessibility settings") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }, 20)
                place(button("Lock again now", ButtonKind.SECONDARY) { lock.lockAgain(); rebuild() }, 10)
            }
        }
    }

    /** Every second: move the ring, or rebuild when the state changes (e.g. wait is over). */
    override fun tick() {
        val state = lock.state()
        if (state.javaClass != builtKind) rebuild() else showRing(state)
    }

    private fun showRing(state: ProtectionLock.State) {
        val r = ring ?: return
        when (state) {
            ProtectionLock.State.Locked -> {
                r.show(0f, mmss(ProtectionLock.WAIT_MS), "wait to unlock", Palette.AMBER)
                r.contentDescription = "Locked. Unlocking takes 10 minutes."
            }
            is ProtectionLock.State.Waiting -> {
                r.show(state.msLeft.toFloat() / ProtectionLock.WAIT_MS, mmss(state.msLeft), "until unlock", Palette.AMBER)
                r.contentDescription = "${mmss(state.msLeft)} until unlock"
            }
            is ProtectionLock.State.Unlocked -> {
                r.show(state.msLeft.toFloat() / ProtectionLock.OPEN_MS, mmss(state.msLeft), "left to switch off", Palette.MINT)
                r.contentDescription = "Unlocked for ${mmss(state.msLeft)}"
            }
        }
    }

    private fun stepsCard(state: ProtectionLock.State): View {
        val current = when (state) {
            ProtectionLock.State.Locked -> 0
            is ProtectionLock.State.Waiting -> 1
            is ProtectionLock.State.Unlocked -> 2
        }
        val labels = listOf(
            "Unlock requested",
            "Wait 10 minutes",
            "Open for 5 minutes: switch off or raise limits",
            "Locks again by itself",
        )
        val card = card(paddingDp = 16)
        labels.forEachIndexed { i, label ->
            val stepState = when {
                i < current -> StepState.DONE
                i == current -> StepState.CURRENT
                else -> StepState.LATER
            }
            val r = row()
            r.addChild(stepDot(stepState, null, 24), dp(24), dp(24))
            r.addChild(
                text(label, if (stepState == StepState.CURRENT) TextStyle.SMALL_BOLD else TextStyle.SMALL,
                    if (stepState == StepState.CURRENT) Palette.TEXT else Palette.MUTED),
                0, WRAP, weight = 1f, start = dp(12),
            )
            card.addChild(r, top = if (i == 0) 0 else dp(14))
        }
        return card
    }

    private fun note(value: String): View =
        text(value, TextStyle.CAPTION, Palette.FAINT).apply { gravity = Gravity.CENTER }
}
