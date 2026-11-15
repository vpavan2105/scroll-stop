package com.example.reelsblocker.ui

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.example.reelsblocker.DailyCounter
import com.example.reelsblocker.ProtectionLock
import com.example.reelsblocker.R

/**
 * Daily limits (design screen 3). Big − / + buttons, 1 to 50.
 * Lowering works any time; raising needs the strict lock to be unlocked.
 * Long-press a button to change by 5.
 */
class LimitsActivity : Screen() {

    override fun build() {
        val unlocked = ProtectionLock(this).isUnlocked()

        placeBackHeader("Daily limits")
        place(
            text("Pick how much you allow yourself each day, from ${DailyCounter.MIN_LIMIT} to ${DailyCounter.MAX_LIMIT}. Changes apply right away.",
                TextStyle.BODY, Palette.MUTED).apply { setPadding(dp(4), 0, dp(4), 0) },
            16,
        )
        place(limitCard("Instagram reels", "Full-screen reels you open from chats, posts or profiles", DailyCounter.reels(this), unlocked), 20)
        place(limitCard("Suggested posts", "Posts and reels from accounts you don't follow, on the normal Home feed", DailyCounter.feedPosts(this), unlocked), 16)
        place(limitCard("YouTube Shorts", "Shorts you open from YouTube's Home feed (the Shorts tab is hidden)", DailyCounter.shorts(this), unlocked), 16)
        place(infoBox(unlocked), 16)
    }

    private fun limitCard(title: String, description: String, counter: DailyCounter, unlocked: Boolean): View {
        val limit = counter.limit
        val card = card(paddingDp = 20, radiusDp = 24)
        card.addChild(text(title, TextStyle.HEADING))
        card.addChild(text(description, TextStyle.SMALL, Palette.MUTED), top = dp(4))

        // − 30 +
        val stepper = row()
        stepper.addChild(
            roundButton(R.drawable.ic_minus, "Lower the $title limit", enabled = limit > DailyCounter.MIN_LIMIT, locked = false) { step ->
                counter.setLimit(limit - step)
                rebuild()
            },
            dp(52), dp(52),
        )
        val value = column().apply { gravity = Gravity.CENTER_HORIZONTAL }
        value.addChild(text("$limit", TextStyle.BIG_NUMBER, Palette.AMBER).apply { gravity = Gravity.CENTER })
        value.addChild(text("per day", TextStyle.CAPTION, Palette.MUTED).apply { gravity = Gravity.CENTER }, top = dp(6))
        stepper.addChild(value, 0, WRAP, weight = 1f)
        stepper.addChild(
            roundButton(R.drawable.ic_plus, "Raise the $title limit", enabled = limit < DailyCounter.MAX_LIMIT, locked = !unlocked) { step ->
                counter.setLimit(limit + step)
                rebuild()
            },
            dp(52), dp(52),
        )
        card.addChild(stepper, top = dp(16))

        // scale 1 … 50
        card.addChild(BarView(this).apply { fraction = limit.toFloat() / DailyCounter.MAX_LIMIT }, MATCH, dp(6), top = dp(16))
        val scale = row()
        scale.addChild(text("${DailyCounter.MIN_LIMIT}", TextStyle.CAPTION, Palette.FAINT), 0, WRAP, weight = 1f)
        scale.addChild(text("${DailyCounter.MAX_LIMIT / 2}", TextStyle.CAPTION, Palette.FAINT).apply { gravity = Gravity.CENTER }, 0, WRAP, weight = 1f)
        scale.addChild(text("${DailyCounter.MAX_LIMIT}", TextStyle.CAPTION, Palette.FAINT).apply { gravity = Gravity.END }, 0, WRAP, weight = 1f)
        card.addChild(scale, top = dp(6))

        card.addChild(text("Used today: ${counter.today()}", TextStyle.SMALL, Palette.MUTED), top = dp(14))
        return card
    }

    /**
     * A 52 dp round − or + button. [onStep] gets 1 for a tap, 5 for a long-press.
     * [locked]: shows a small lock and explains instead of changing anything.
     */
    private fun roundButton(iconRes: Int, label: String, enabled: Boolean, locked: Boolean, onStep: (Int) -> Unit): View =
        FrameLayout(this).apply {
            contentDescription = if (locked) "$label (needs an unlock)" else label
            isClickable = true
            isFocusable = true
            val dim = locked || !enabled
            val shape = circle(if (dim) Palette.SUNKEN else Palette.SURFACE_2, if (dim) Palette.BORDER else Palette.OUTLINE, dp(1))
            background = withRipple(shape, circle(Palette.TEXT))
            addView(icon(iconRes, if (dim) Palette.DISABLED else Palette.TEXT), FrameLayout.LayoutParams(dp(22), dp(22), Gravity.CENTER))
            if (locked) {
                val badge = FrameLayout(context).apply {
                    background = circle(Palette.SURFACE_3, Palette.SURFACE, dp(2))
                    addView(icon(R.drawable.ic_lock, Palette.MUTED), FrameLayout.LayoutParams(dp(11), dp(11), Gravity.CENTER))
                }
                addView(badge, FrameLayout.LayoutParams(dp(20), dp(20), Gravity.BOTTOM or Gravity.END))
            }
            fun press(step: Int) {
                when {
                    locked -> toast("Raising a limit needs an unlock first. Lowering always works.")
                    enabled -> onStep(step)
                }
            }
            setOnClickListener { press(1) }
            setOnLongClickListener { press(5); true }
        }

    private fun infoBox(unlocked: Boolean): View {
        val box = row().apply {
            gravity = Gravity.TOP
            background = rounded(Palette.INFO, dp(16).toFloat())
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }
        box.addChild(icon(R.drawable.ic_lock, Palette.MUTED), dp(20), dp(20))
        val texts = column()
        if (unlocked) {
            texts.addChild(text("Unlocked: you can raise limits now. It locks again by itself.", TextStyle.SMALL, Palette.TEXT_SOFT))
        } else {
            texts.addChild(text("Lowering a limit works any time. Raising one needs an unlock first.", TextStyle.SMALL, Palette.TEXT_SOFT))
            texts.addChild(
                text("Request unlock", TextStyle.SMALL_BOLD, Palette.AMBER).apply {
                    isClickable = true
                    isFocusable = true
                    minHeight = dp(44)
                    gravity = Gravity.CENTER_VERTICAL
                    setOnClickListener { open(UnlockActivity::class.java) }
                },
                WRAP, WRAP,
            )
        }
        box.addChild(texts, 0, WRAP, weight = 1f, start = dp(12))
        return box
    }
}
