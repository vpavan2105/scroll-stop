package com.example.reelsblocker.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import com.example.reelsblocker.R

/**
 * Base class for every screen. It gives each screen:
 *  - a scrolling page with the right padding and background
 *  - [build]: called every time the screen is shown, to (re)create its content
 *    from the latest data (counts, lock state…)
 *  - [tick]: called once a second while visible, if [tickEverySecond] is true
 *    (for countdowns)
 */
abstract class Screen : Activity() {

    protected val handler = Handler(Looper.getMainLooper())
    protected lateinit var page: LinearLayout
    protected open val tickEverySecond = false

    private val ticker = object : Runnable {
        override fun run() {
            tick()
            handler.postDelayed(this, 1_000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = column().apply { setPadding(dp(20), dp(16), dp(20), dp(28)) }
        setContentView(ScrollView(this).apply {
            fitsSystemWindows = true              // keep clear of the status and navigation bars
            isFillViewport = true                 // lets a spacer push buttons to the bottom
            setBackgroundColor(Palette.INK)
            addView(page)
        })
    }

    override fun onResume() {
        super.onResume()
        rebuild()
        if (tickEverySecond) handler.post(ticker)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(ticker)
    }

    /** Throw away the content and build it again from fresh data. */
    protected fun rebuild() {
        page.removeAllViews()
        build()
    }

    protected abstract fun build()

    protected open fun tick() {}

    /** Adds a block to the page with [gapDp] of space above it. */
    protected fun place(view: View, gapDp: Int = 20) {
        page.addChild(view, top = if (page.childCount == 0) 0 else dp(gapDp))
    }

    /** Adds empty space that grows, so whatever comes after sits at the bottom. */
    protected fun placeSpacer() {
        page.addChild(spacer(), MATCH, 0, weight = 1f)
    }

    /** A back arrow and the screen title. */
    protected fun placeBackHeader(title: String) {
        val header = row()
        val back = FrameLayout(this).apply {
            contentDescription = "Back"
            isClickable = true
            isFocusable = true
            background = withRipple(rounded(0x00000000, dp(12).toFloat()), rounded(Palette.TEXT, dp(12).toFloat()))
            addView(icon(R.drawable.ic_back), FrameLayout.LayoutParams(dp(22), dp(22), Gravity.CENTER))
            setOnClickListener { finish() }
        }
        header.addChild(back, dp(44), dp(44))
        header.addChild(text(title, TextStyle.TITLE), 0, WRAP, weight = 1f, start = dp(8))
        place(header, 0)
    }

    protected fun open(screen: Class<out Activity>) = startActivity(Intent(this, screen))

    protected fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
