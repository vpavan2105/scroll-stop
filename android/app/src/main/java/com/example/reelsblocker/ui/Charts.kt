package com.example.reelsblocker.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.example.reelsblocker.R

/**
 * A progress ring with a big number in the middle ("12" / "of 30").
 * Drawn by hand on a Canvas: Android has no ready-made ring like this.
 */
class RingView(context: Context, private val sizeDp: Int, strokeDp: Int) : View(context) {

    var fraction = 0f
    var color = Palette.AMBER
    var trackColor = Palette.TRACK
    var bigText = ""
    var smallText = ""
    var bigTextSp = 34f

    private val density = resources.displayMetrics.density
    private val stroke = strokeDp * density
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = stroke
        strokeCap = Paint.Cap.ROUND
    }
    private val bigPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = context.font(R.font.space_grotesk_semibold)
        color = Palette.TEXT
    }
    private val smallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = context.font(R.font.inter_regular)
        color = Palette.MUTED
        textSize = 13 * resources.displayMetrics.scaledDensity
    }
    private val rect = RectF()

    /** Change what the ring shows, then redraw. */
    fun show(fraction: Float, big: String, small: String, color: Int = this.color) {
        this.fraction = fraction.coerceIn(0f, 1f)
        this.bigText = big
        this.smallText = small
        this.color = color
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = (sizeDp * density).toInt()
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        val half = stroke / 2
        rect.set(half, half, width - half, height - half)

        arcPaint.color = trackColor
        canvas.drawArc(rect, 0f, 360f, false, arcPaint)
        if (fraction > 0f) {
            arcPaint.color = color
            canvas.drawArc(rect, -90f, 360f * fraction, false, arcPaint)   // starts at 12 o'clock
        }

        bigPaint.textSize = bigTextSp * resources.displayMetrics.scaledDensity
        val cx = width / 2f
        val cy = height / 2f
        canvas.drawText(bigText, cx, cy + bigPaint.textSize * 0.3f, bigPaint)
        if (smallText.isNotEmpty()) {
            canvas.drawText(smallText, cx, cy + bigPaint.textSize * 0.3f + smallPaint.textSize * 1.6f, smallPaint)
        }
    }
}

/** A thin horizontal progress bar (used for the 1–50 limit scale). */
class BarView(context: Context) : View(context) {

    var fraction = 0f
        set(value) { field = value.coerceIn(0f, 1f); invalidate() }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        val r = height / 2f
        paint.color = Palette.TRACK
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, r, r, paint)
        paint.color = Palette.AMBER
        rect.set(0f, 0f, width * fraction, height.toFloat())
        canvas.drawRoundRect(rect, r, r, paint)
    }
}
