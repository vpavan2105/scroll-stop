package com.example.reelsblocker.ui

import android.content.Intent
import android.view.Gravity
import android.view.View
import com.example.reelsblocker.ErrorLog
import com.example.reelsblocker.InstagramHealthCheck
import com.example.reelsblocker.InstagramIds
import com.example.reelsblocker.LearnedIds
import com.example.reelsblocker.Notifier
import com.example.reelsblocker.R
import com.example.reelsblocker.ReelsBlockerService
import com.example.reelsblocker.ScreenDumper
import com.example.reelsblocker.TeachTarget

/**
 * After an Instagram update (design screen 5): the "Instagram changed"
 * warning, teach mode ("Re-learn") and diagnostics.
 */
class RepairActivity : Screen() {

    override fun build() {
        val health = InstagramHealthCheck.snapshot(this)
        val learned = LearnedIds(this)

        placeBackHeader("After an app update")

        if (health.problem) {
            val missing = listOfNotNull(
                "Reels".takeIf { !health.reelsFound },
                "Explore".takeIf { !health.exploreFound },
            ).joinToString(" and ")
            val lastOk = health.lastWorkingVersion?.let { " It last worked with $it." } ?: ""
            place(warningBanner("Instagram changed", "Can't find the $missing button in Instagram ${health.instagramVersion}.$lastOk Tap Re-learn below."), 16)
        }

        place(sectionLabel("Re-teach: Instagram"), 20)
        fun status(target: TeachTarget, found: Boolean?): Pair<String, Int> = when {
            learned.get(target) != null -> "Learned" to Palette.MINT
            found == null -> "Using built-in name" to Palette.FAINT
            !health.checked -> "Not checked yet" to Palette.FAINT
            found == true -> "Found" to Palette.MINT
            else -> "Not found" to Palette.AMBER
        }
        place(listCard(listOf(
            teachRow(TeachTarget.REELS_TAB, status(TeachTarget.REELS_TAB, health.reelsFound)),
            teachRow(TeachTarget.EXPLORE_TAB, status(TeachTarget.EXPLORE_TAB, health.exploreFound)),
            teachRow(TeachTarget.REELS_PLAYER, status(TeachTarget.REELS_PLAYER, null)),
        )), 10)

        place(sectionLabel("Re-teach: YouTube"), 20)
        place(listCard(listOf(
            teachRow(TeachTarget.SHORTS_TAB, status(TeachTarget.SHORTS_TAB, null)),
            teachRow(TeachTarget.SHORTS_PLAYER, status(TeachTarget.SHORTS_PLAYER, null)),
        )), 10)

        // How teaching works, in three tiles
        val tiles = row().apply { gravity = Gravity.TOP }
        listOf("Tap Re-learn", "The app opens", "Tap that button once within 30 s").forEachIndexed { i, label ->
            val tile = column().apply {
                background = rounded(Palette.SUNKEN, dp(14).toFloat())
                setPadding(dp(12), dp(12), dp(12), dp(12))
                addChild(text("${i + 1}", TextStyle.HEADING, Palette.AMBER).apply { typeface = font(R.font.space_grotesk_semibold); textSize = 18f })
                addChild(text(label, TextStyle.CAPTION, Palette.TEXT_SOFT), top = dp(6))
            }
            tiles.addChild(tile, 0, MATCH, weight = 1f, start = if (i > 0) dp(8) else 0)
        }
        place(tiles, 14)

        // Diagnostics
        place(sectionLabel("Diagnostics"), 24)
        val diag = card(paddingDp = 0).apply { setPadding(dp(16), dp(4), dp(16), dp(4)) }
        listOf(
            "Instagram version" to health.instagramVersion,
            "Last version that worked" to (health.lastWorkingVersion ?: "Not seen yet"),
            "Last error" to (ErrorLog.last(this) ?: "None"),
            "Notifications" to if (Notifier.allowed(this)) "Allowed" else "Not allowed",
        ).forEachIndexed { i, (label, value) ->
            if (i > 0) diag.addChild(divider(), MATCH, 1)
            val r = row().apply { gravity = Gravity.TOP; setPadding(0, dp(11), 0, dp(11)) }
            r.addChild(text(label, TextStyle.SMALL, Palette.MUTED), WRAP, WRAP)
            r.addChild(text(value, TextStyle.SMALL).apply { gravity = Gravity.END }, 0, WRAP, weight = 1f, start = dp(12))
            diag.addChild(r)
        }
        place(diag, 10)

        val actions = row()
        actions.addChild(button("Capture screen", ButtonKind.OUTLINE, 48) { capture() }, 0, WRAP, weight = 1f)
        actions.addChild(button("Share capture", ButtonKind.OUTLINE, 48) { shareCapture() }, 0, WRAP, weight = 1f, start = dp(10))
        place(actions, 10)
        place(button("Forget learned names", ButtonKind.GHOST, 44) {
            learned.clear()
            rebuild()
        }, 6)
    }

    private fun teachRow(target: TeachTarget, status: Pair<String, Int>): View {
        val (label, color) = status
        val r = row().apply { setPadding(dp(16), dp(14), dp(16), dp(14)) }
        val texts = column()
        texts.addChild(text(target.title, TextStyle.BODY_MEDIUM))
        val statusRow = row()
        statusRow.addChild(icon(if (color == Palette.AMBER) R.drawable.ic_alert else R.drawable.ic_check, color), dp(14), dp(14))
        statusRow.addChild(text(label, TextStyle.CAPTION_BOLD, color), WRAP, WRAP, start = dp(6))
        texts.addChild(statusRow, top = dp(3))
        r.addChild(texts, 0, WRAP, weight = 1f)
        val kind = if (label == "Not found") ButtonKind.PRIMARY else ButtonKind.OUTLINE
        r.addChild(button("Re-learn", kind, 44) { startTeaching(target) }, WRAP, WRAP, start = dp(12))
        return r
    }

    private fun startTeaching(target: TeachTarget) {
        val service = ReelsBlockerService.instance
        if (service == null) { toast("Turn protection on first"); return }
        service.startTeaching(target)
        toast(
            when (target) {
                TeachTarget.REELS_PLAYER -> "Open any reel full screen and swipe up once."
                TeachTarget.SHORTS_PLAYER -> "Open any Short from the Home feed and swipe up once."
                else -> "Tap the ${target.title} once."
            }
        )
        openApp(target.packageName)
    }

    private fun capture() {
        val service = ReelsBlockerService.instance
        if (service == null) { toast("Turn protection on first"); return }
        service.captureScreenAfter(5_000)
        toast("Capturing in 5 seconds. Go to the Instagram screen you want to capture.")
        openApp(InstagramIds.PACKAGE)
    }

    private fun openApp(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let { startActivity(it) }
    }

    private fun shareCapture() {
        val file = ScreenDumper.file(this)
        if (!file.exists()) { toast("No capture yet"); return }
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Instagram screen layout")
            putExtra(Intent.EXTRA_TEXT, file.readText().take(100_000))
        }
        startActivity(Intent.createChooser(send, "Share capture"))
    }
}
