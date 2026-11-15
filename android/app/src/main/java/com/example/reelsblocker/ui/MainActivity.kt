package com.example.reelsblocker.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.example.reelsblocker.DailyCounter
import com.example.reelsblocker.InstagramHealthCheck
import com.example.reelsblocker.ProtectionLock
import com.example.reelsblocker.R
import com.example.reelsblocker.ServiceStatus
import com.example.reelsblocker.WatchdogJobService

/**
 * Home (design screen 2): today's numbers first, then what is blocked
 * inside Instagram, the strict lock, and the repair screen.
 */
class MainActivity : Screen() {

    companion object {
        private var setupShownThisRun = false
    }

    override val tickEverySecond = true

    private lateinit var lock: ProtectionLock
    private var lockBody: TextView? = null
    private var builtLockKind: Class<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lock = ProtectionLock(this)
        WatchdogJobService.schedule(this)
        // First time, and protection isn't switched on yet: show the setup steps.
        if (!ServiceStatus.isEnabled(this) && !setupShownThisRun) {
            setupShownThisRun = true
            open(SetupActivity::class.java)
        }
    }

    override fun build() {
        val running = ServiceStatus.isRunning(this)
        val enabled = ServiceStatus.isEnabled(this)
        val reels = DailyCounter.reels(this)
        val feed = DailyCounter.feedPosts(this)
        val shorts = DailyCounter.shorts(this)
        val health = InstagramHealthCheck.snapshot(this)

        // Header: name + status in words
        val header = row()
        header.addChild(text("ScrollStop", TextStyle.BRAND), 0, WRAP, weight = 1f)
        header.addChild(
            when {
                running -> chip("Protection on", Palette.MINT, Palette.MINT_BG, R.drawable.ic_shield_check)
                enabled -> chip("Not running", Palette.AMBER, Palette.AMBER_BG, R.drawable.ic_alert)
                else -> chip("Protection off", Palette.RED, Palette.RED_BG, R.drawable.ic_alert)
            },
            WRAP, WRAP,
        )
        place(header, 0)

        if (!running) place(protectionOffBanner(enabled), 16)

        val today = column()
        today.addChild(text("Today", TextStyle.DISPLAY))
        today.addChild(text("Counts reset at midnight", TextStyle.BODY, Palette.MUTED), top = dp(4))
        place(today, 22)

        place(todayCard(reels, feed, shorts), 20)

        place(sectionLabel("Inside Instagram"), 24)
        place(rulesCard(running, health.reelsFound, health.exploreFound, reels, feed), 10)

        place(sectionLabel("Inside YouTube"), 24)
        place(listCard(listOf(
            listRow(R.drawable.ic_reels_off, "Shorts tab",
                if (running) chip("Hidden", Palette.MINT, Palette.MINT_BG) else chip("Off", Palette.RED, Palette.RED_BG)),
            listRow(R.drawable.ic_chat_reel, "Shorts you open", text("${shorts.limit} a day", TextStyle.SMALL_BOLD, Palette.AMBER)),
        )), 10)

        place(lockCard(), 20)
        place(repairRow(health.problem, health.checked, health.instagramVersion), 12)

        placeSpacer()
        val footer = row().apply { gravity = Gravity.CENTER }
        footer.addChild(icon(R.drawable.ic_wifi_off, Palette.FAINT), dp(16), dp(16))
        footer.addChild(text("No internet access. Everything stays on this phone.", TextStyle.CAPTION, Palette.FAINT), WRAP, WRAP, start = dp(8))
        place(footer, 24)
    }

    /** Keeps the lock card's countdown live without rebuilding the whole page. */
    override fun tick() {
        val state = lock.state()
        if (state.javaClass != builtLockKind) { rebuild(); return }
        lockBody?.text = lockBodyText(state)
    }

    // ---------------------------------------------------------------- blocks

    private fun protectionOffBanner(enabled: Boolean): View = column().apply {
        addChild(
            if (enabled) warningBanner("Protection stopped", "Android stopped the blocker. Turn it off and on again in Accessibility settings.")
            else warningBanner("Protection is off", "Reels and Explore are visible again. Turn it on to keep your limits."),
        )
        addChild(
            button(if (enabled) "Open Accessibility settings" else "Turn it on") {
                if (enabled) startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                else open(SetupActivity::class.java)
            },
            top = dp(10),
        )
    }

    private fun todayCard(reels: DailyCounter, feed: DailyCounter, shorts: DailyCounter): View {
        val card = card(paddingDp = 18, radiusDp = 24)
        val rings = row().apply { gravity = Gravity.TOP }
        rings.addChild(ringWithLabel(reels.today(), reels.limit, "Reels"), 0, WRAP, weight = 1f)
        rings.addChild(ringWithLabel(feed.today(), feed.limit, "Suggested posts"), 0, WRAP, weight = 1f, start = dp(8))
        rings.addChild(ringWithLabel(shorts.today(), shorts.limit, "Shorts"), 0, WRAP, weight = 1f, start = dp(8))
        card.addChild(rings)
        card.addChild(divider(), MATCH, 1, top = dp(18))

        val left = listOf(reels, feed, shorts).sumOf { (it.limit - it.today()).coerceAtLeast(0) }
        val summary = if (left == 0) "All used up for today" else "$left left today in total"
        val bottom = row()
        bottom.addChild(text(summary, TextStyle.SMALL, Palette.MUTED), 0, WRAP, weight = 1f)
        bottom.addChild(button("Edit limits", ButtonKind.SECONDARY, heightDp = 44) { open(LimitsActivity::class.java) }, WRAP, WRAP, start = dp(12))
        card.addChild(bottom, top = dp(14))
        return card
    }

    private fun ringWithLabel(used: Int, limit: Int, label: String): View = column().apply {
        gravity = Gravity.CENTER_HORIZONTAL
        val ring = RingView(context, sizeDp = 96, strokeDp = 8).apply { bigTextSp = 26f }
        ring.show(used.toFloat() / limit, "$used", "of $limit")
        ring.contentDescription = "$label: $used of $limit used today"
        addChild(ring, WRAP, WRAP, gravity = Gravity.CENTER_HORIZONTAL)
        addChild(text(label, TextStyle.CAPTION_BOLD).apply { gravity = Gravity.CENTER }, top = dp(10))
    }

    private fun rulesCard(running: Boolean, reelsFound: Boolean, exploreFound: Boolean, reels: DailyCounter, feed: DailyCounter): View {
        fun tabState(found: Boolean): View = when {
            !running -> chip("Off", Palette.RED, Palette.RED_BG)
            !found -> chip("Not found", Palette.AMBER, Palette.AMBER_BG)
            else -> chip("Hidden", Palette.MINT, Palette.MINT_BG)
        }
        fun limitText(limit: Int) = text("$limit a day", TextStyle.SMALL_BOLD, Palette.AMBER)

        return listCard(listOf(
            listRow(R.drawable.ic_reels_off, "Reels tab", tabState(reelsFound)),
            listRow(R.drawable.ic_compass, "Explore tab", tabState(exploreFound)),
            listRow(R.drawable.ic_following, "Opening Instagram", text("Shows Following", TextStyle.SMALL, Palette.MUTED)),
            listRow(R.drawable.ic_feed, "Suggested posts", limitText(feed.limit)),
            listRow(R.drawable.ic_chat_reel, "Reels from chats", limitText(reels.limit)),
        ))
    }

    private fun lockCard(): View {
        val state = lock.state()
        builtLockKind = state.javaClass

        val card = card()
        val top = row().apply { gravity = Gravity.TOP }
        top.addChild(iconTile(R.drawable.ic_lock), dp(40), dp(40))
        val texts = column()
        val title = when (state) {
            ProtectionLock.State.Locked -> "Strict lock is on"
            is ProtectionLock.State.Waiting -> "Unlock requested"
            is ProtectionLock.State.Unlocked -> "Unlocked"
        }
        texts.addChild(text(title, TextStyle.HEADING))
        lockBody = text(lockBodyText(state), TextStyle.SMALL, Palette.MUTED)
        texts.addChild(lockBody!!, top = dp(4))
        top.addChild(texts, 0, WRAP, weight = 1f, start = dp(14))
        card.addChild(top)

        val action = when (state) {
            ProtectionLock.State.Locked -> button("Request unlock", ButtonKind.OUTLINE, 48) { open(UnlockActivity::class.java) }
            is ProtectionLock.State.Waiting -> button("View timer", ButtonKind.OUTLINE, 48) { open(UnlockActivity::class.java) }
            is ProtectionLock.State.Unlocked -> button("Lock again now", ButtonKind.OUTLINE, 48) {
                lock.lockAgain()
                rebuild()
            }
        }
        card.addChild(action, top = dp(14))
        return card
    }

    private fun lockBodyText(state: ProtectionLock.State): String = when (state) {
        ProtectionLock.State.Locked ->
            "Switching protection off or raising a limit needs a 10-minute wait. Uninstalling is always allowed."
        is ProtectionLock.State.Waiting -> "Opens in ${mmss(state.msLeft)}."
        is ProtectionLock.State.Unlocked ->
            "You can switch protection off or raise limits for ${mmss(state.msLeft)}."
    }

    private fun repairRow(problem: Boolean, checked: Boolean, version: String): View {
        val row = clickableCard { open(RepairActivity::class.java) }
        row.addChild(iconTile(R.drawable.ic_wrench), dp(40), dp(40))
        val texts = column()
        texts.addChild(text("After an app update", TextStyle.HEADING))
        val (subtitle, color) = when {
            problem -> "Instagram changed · tap to fix" to Palette.AMBER
            checked -> "All buttons found · Instagram $version" to Palette.MUTED
            else -> "Not checked yet · open Instagram once" to Palette.MUTED
        }
        texts.addChild(text(subtitle, TextStyle.CAPTION, color), top = dp(2))
        row.addChild(texts, 0, WRAP, weight = 1f, start = dp(14))
        row.addChild(icon(R.drawable.ic_chevron_right, Palette.FAINT), dp(18), dp(18), start = dp(8))
        return row
    }
}
