package com.example.reelsblocker

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

/**
 * A plain rectangle drawn ON TOP of Instagram over one area of the screen
 * (a tab button, or the suggested feed). It is painted in Instagram's
 * background colour and swallows taps, so whatever is under it is gone.
 *
 * Optional: [content] builds a designed panel to show inside the cover
 * (the "limit reached" panel), and [onTap] is called when it's tapped.
 *
 * TYPE_ACCESSIBILITY_OVERLAY is a window type only accessibility services may
 * use. It needs no "draw over other apps" permission.
 */
class TabCover(
    private val service: AccessibilityService,
    private val content: ((Context) -> View)? = null,
    private val onTap: (() -> Unit)? = null,
) {

    private val windowManager = service.getSystemService(WindowManager::class.java)
    private var view: View? = null
    private var currentBounds: Rect? = null

    val isShowing: Boolean get() = view != null

    /** Show (or move) the cover so it sits exactly over [bounds] (screen pixels). */
    @SuppressLint("ClickableViewAccessibility")
    fun showOver(bounds: Rect) {
        if (bounds.isEmpty) { hide(); return }
        if (bounds == currentBounds) return            // already in the right place

        val params = WindowManager.LayoutParams(
            bounds.width(),
            bounds.height(),
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or        // never steal the keyboard
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or  // taps outside go to Instagram
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or // x/y are screen coordinates
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = bounds.left
            y = bounds.top
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        val existing = view
        if (existing == null) {
            val v = createView()
            if (content == null) v.setBackgroundColor(backgroundColor())
            runCatching { windowManager.addView(v, params) }.onSuccess { view = v }
        } else {
            if (content == null) existing.setBackgroundColor(backgroundColor())
            runCatching { windowManager.updateViewLayout(existing, params) }
        }
        currentBounds = Rect(bounds)
    }

    fun hide() {
        view?.let { runCatching { windowManager.removeView(it) } }
        view = null
        currentBounds = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createView(): View {
        // A new panel is built each time the cover appears, so it shows today's limit.
        val v: View = content?.invoke(service) ?: View(service)
        v.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) onTap?.invoke()
            true                                       // eat every touch on the cover
        }
        return v
    }

    /** Instagram follows the phone's dark mode: black in dark mode, white otherwise. */
    private fun backgroundColor(): Int {
        val night = service.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        return if (night) Color.BLACK else Color.WHITE
    }
}
