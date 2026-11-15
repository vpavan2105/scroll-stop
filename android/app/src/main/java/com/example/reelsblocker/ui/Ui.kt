package com.example.reelsblocker.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.reelsblocker.R

/*
 * The app's small "UI kit": colours, text styles and ready-made pieces
 * (cards, chips, buttons, rows). Every screen is built from these, so the
 * whole app looks the same and a colour changes in one place.
 *
 * Built in code instead of XML layouts to keep the project small and to
 * show clearly how Android views are put together.
 */

/** Colours from the design canvas. */
object Palette {
    const val INK = 0xFF0E0F13.toInt()            // page background
    const val SURFACE = 0xFF17191F.toInt()        // cards
    const val SURFACE_2 = 0xFF1F222A.toInt()      // icon tiles, round buttons
    const val SURFACE_3 = 0xFF23262E.toInt()      // secondary buttons
    const val SUNKEN = 0xFF15171C.toInt()         // disabled buttons, step tiles
    const val INFO = 0xFF1A1C22.toInt()           // info boxes
    const val BORDER = 0xFF23262E.toInt()
    const val DIVIDER = 0xFF22252D.toInt()
    const val OUTLINE = 0xFF343844.toInt()
    const val TRACK = 0xFF262A33.toInt()          // empty part of rings and bars

    const val TEXT = 0xFFF2F3F5.toInt()
    const val TEXT_SOFT = 0xFFC9CDD6.toInt()
    const val MUTED = 0xFFA3A8B4.toInt()
    const val FAINT = 0xFF8B91A0.toInt()
    const val DISABLED = 0xFF6E7483.toInt()

    const val AMBER = 0xFFF5B13D.toInt()          // limits, primary buttons
    const val ON_AMBER = 0xFF1A1408.toInt()
    const val AMBER_BG = 0xFF2A2111.toInt()
    const val AMBER_BORDER = 0xFF4A3818.toInt()
    const val AMBER_TITLE = 0xFFFFD58A.toInt()
    const val AMBER_BODY = 0xFFE8DCC4.toInt()

    const val MINT = 0xFF5BD6A5.toInt()           // "protected" / done
    const val MINT_BG = 0xFF13271F.toInt()

    const val RED = 0xFFFF9A8F.toInt()            // protection off
    const val RED_BG = 0xFF2B1517.toInt()
}

/** Text styles from the design: font file, size, spacing. */
enum class TextStyle(
    val sizeSp: Float,
    val font: Int,
    val letterSpacing: Float = 0f,
    val lineMultiplier: Float = 1.15f,
    val uppercase: Boolean = false,
) {
    DISPLAY(34f, R.font.space_grotesk_semibold, -0.018f),
    HERO(30f, R.font.space_grotesk_semibold, -0.016f),
    TITLE(24f, R.font.space_grotesk_semibold, -0.012f),
    BRAND(20f, R.font.space_grotesk_bold, -0.01f),
    BIG_NUMBER(56f, R.font.space_grotesk_semibold),
    HEADING(16f, R.font.inter_semibold),
    BODY(15f, R.font.inter_regular, lineMultiplier = 1.4f),
    BODY_MEDIUM(15f, R.font.inter_medium),
    SMALL(14f, R.font.inter_regular, lineMultiplier = 1.35f),
    SMALL_BOLD(14f, R.font.inter_semibold),
    CAPTION(13f, R.font.inter_regular, lineMultiplier = 1.35f),
    CAPTION_BOLD(13f, R.font.inter_semibold),
    LABEL(13f, R.font.inter_semibold, letterSpacing = 0.05f, uppercase = true),
}

const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

private val fontCache = HashMap<Int, Typeface>()

/** Loads a font from res/font once and keeps it. */
fun Context.font(res: Int): Typeface = fontCache.getOrPut(res) { resources.getFont(res) }

/** Converts design "dp" units to screen pixels. */
fun Context.dp(value: Number): Int = (value.toFloat() * resources.displayMetrics.density).toInt()

// ------------------------------------------------------------------ layout helpers

/** Adds [child] to this vertical/horizontal layout with size, weight and margins (px). */
fun LinearLayout.addChild(
    child: View,
    width: Int = MATCH,
    height: Int = WRAP,
    weight: Float = 0f,
    start: Int = 0,
    top: Int = 0,
    gravity: Int = -1,
): View {
    val params = LinearLayout.LayoutParams(width, height, weight).apply {
        marginStart = start
        topMargin = top
        if (gravity != -1) this.gravity = gravity
    }
    addView(child, params)
    return child
}

fun Context.column(): LinearLayout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

fun Context.row(): LinearLayout = LinearLayout(this).apply {
    orientation = LinearLayout.HORIZONTAL
    gravity = Gravity.CENTER_VERTICAL
}

/** An empty view that takes up the leftover height (pushes buttons to the bottom). */
fun Context.spacer(): View = View(this)

fun Context.divider(): View = View(this).apply { setBackgroundColor(Palette.DIVIDER) }

/** A rounded rectangle, optionally with a border. */
fun rounded(color: Int, radiusPx: Float, strokeColor: Int? = null, strokePx: Int = 0): GradientDrawable =
    GradientDrawable().apply {
        setColor(color)
        cornerRadius = radiusPx
        if (strokeColor != null) setStroke(strokePx, strokeColor)
    }

fun circle(color: Int, strokeColor: Int? = null, strokePx: Int = 0): GradientDrawable =
    GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(color)
        if (strokeColor != null) setStroke(strokePx, strokeColor)
    }

/** Adds the touch "ripple" effect on top of a background. */
fun withRipple(background: Drawable, mask: Drawable): Drawable =
    RippleDrawable(ColorStateList.valueOf(0x22FFFFFF), background, mask)

// ------------------------------------------------------------------ pieces

fun Context.text(value: CharSequence, style: TextStyle = TextStyle.BODY, color: Int = Palette.TEXT): TextView =
    TextView(this).apply {
        text = if (style.uppercase) value.toString().uppercase() else value
        setTextSize(TypedValue.COMPLEX_UNIT_SP, style.sizeSp)
        typeface = font(style.font)
        setTextColor(color)
        letterSpacing = style.letterSpacing
        setLineSpacing(0f, style.lineMultiplier)
        includeFontPadding = false
    }

/** A small grey uppercase heading above a group of rows. */
fun Context.sectionLabel(value: String): TextView =
    text(value, TextStyle.LABEL, Palette.FAINT).apply { setPadding(dp(4), 0, dp(4), 0) }

/** A tinted line icon from res/drawable. */
fun Context.icon(res: Int, color: Int = Palette.TEXT): ImageView = ImageView(this).apply {
    setImageResource(res)
    imageTintList = ColorStateList.valueOf(color)
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO   // decoration only
}

/** A rounded square with an icon in the middle (left side of list rows). */
fun Context.iconTile(
    res: Int,
    bg: Int = Palette.SURFACE_2,
    fg: Int = Palette.TEXT,
    sizeDp: Int = 40,
    iconDp: Int = 20,
): FrameLayout = FrameLayout(this).apply {
    background = rounded(bg, dp(12).toFloat())
    addView(icon(res, fg), FrameLayout.LayoutParams(dp(iconDp), dp(iconDp), Gravity.CENTER))
    minimumWidth = dp(sizeDp)
    minimumHeight = dp(sizeDp)
}

/** A card: rounded surface with a thin border. */
fun Context.card(
    paddingDp: Int = 18,
    radiusDp: Int = 20,
    color: Int = Palette.SURFACE,
    stroke: Int? = Palette.BORDER,
): LinearLayout = column().apply {
    background = rounded(color, dp(radiusDp).toFloat(), stroke, dp(1))
    setPadding(dp(paddingDp), dp(paddingDp), dp(paddingDp), dp(paddingDp))
}

/** A pill with optional icon, e.g. "Protection on" or "Hidden". The words carry the meaning, not the colour. */
fun Context.chip(label: String, fg: Int, bg: Int, iconRes: Int? = null): LinearLayout = row().apply {
    background = rounded(bg, dp(999).toFloat())
    setPadding(dp(if (iconRes != null) 12 else 10), dp(6), dp(12), dp(6))
    if (iconRes != null) addChild(icon(iconRes, fg), dp(16), dp(16))
    addChild(text(label, TextStyle.CAPTION_BOLD, fg), WRAP, WRAP, start = if (iconRes != null) dp(8) else 0)
}

enum class ButtonKind { PRIMARY, SECONDARY, OUTLINE, GHOST }

/** The app's buttons. PRIMARY = amber, SECONDARY = grey, OUTLINE = border only, GHOST = text only. */
fun Context.button(
    label: String,
    kind: ButtonKind = ButtonKind.PRIMARY,
    heightDp: Int = 52,
    onClick: () -> Unit,
): Button = Button(this).apply {
    text = label
    setAllCaps(false)
    stateListAnimator = null                       // no default shadow
    minWidth = 0
    minimumWidth = 0
    minHeight = dp(heightDp)
    minimumHeight = dp(heightDp)
    setPadding(dp(18), 0, dp(18), 0)
    setTextSize(TypedValue.COMPLEX_UNIT_SP, if (kind == ButtonKind.PRIMARY) 16f else 15f)
    typeface = font(if (kind == ButtonKind.PRIMARY) R.font.inter_bold else R.font.inter_semibold)
    val radius = dp(16).toFloat()
    val (bg, fg, stroke) = when (kind) {
        ButtonKind.PRIMARY -> Triple(Palette.AMBER, Palette.ON_AMBER, null)
        ButtonKind.SECONDARY -> Triple(Palette.SURFACE_3, Palette.TEXT, null)
        ButtonKind.OUTLINE -> Triple(0x00000000, Palette.TEXT, Palette.OUTLINE)
        ButtonKind.GHOST -> Triple(0x00000000, Palette.MUTED, null)
    }
    setTextColor(fg)
    background = withRipple(rounded(bg, radius, stroke, dp(1)), rounded(Palette.TEXT, radius))
    setOnClickListener { onClick() }
}

/** A tappable card-like row with a ripple (used for "After an Instagram update"). */
fun Context.clickableCard(onClick: () -> Unit): LinearLayout = row().apply {
    val radius = dp(20).toFloat()
    background = withRipple(rounded(Palette.SURFACE, radius, Palette.BORDER, dp(1)), rounded(Palette.TEXT, radius))
    setPadding(dp(16), dp(16), dp(16), dp(16))
    isClickable = true
    isFocusable = true
    setOnClickListener { onClick() }
}

/** A warning box (amber), e.g. "Instagram changed" or "Protection is off". */
fun Context.warningBanner(title: String, body: String): LinearLayout = row().apply {
    gravity = Gravity.TOP
    background = rounded(Palette.AMBER_BG, dp(18).toFloat(), Palette.AMBER_BORDER, dp(1))
    setPadding(dp(16), dp(16), dp(16), dp(16))
    addChild(icon(R.drawable.ic_alert, Palette.AMBER), dp(22), dp(22))
    val texts = column()
    texts.addChild(text(title, TextStyle.BODY_MEDIUM, Palette.AMBER_TITLE).apply { typeface = font(R.font.inter_semibold) })
    texts.addChild(text(body, TextStyle.SMALL, Palette.AMBER_BODY), top = dp(4))
    addChild(texts, 0, WRAP, weight = 1f, start = dp(12))
}

/** One row inside a list card: icon tile, title, and something on the right. */
fun Context.listRow(iconRes: Int, title: String, trailing: View?): LinearLayout = row().apply {
    setPadding(dp(16), dp(14), dp(16), dp(14))
    addChild(iconTile(iconRes), dp(40), dp(40))
    addChild(text(title, TextStyle.BODY_MEDIUM), 0, WRAP, weight = 1f, start = dp(14))
    if (trailing != null) addChild(trailing, WRAP, WRAP, start = dp(10))
}

/** A list card: rows separated by thin lines. */
fun Context.listCard(rows: List<View>): LinearLayout = card(paddingDp = 0).apply {
    rows.forEachIndexed { i, r ->
        if (i > 0) addChild(divider(), MATCH, 1)
        addChild(r)
    }
}

/** Step indicator for checklists: done (mint tick), current (amber ring) or later (grey ring). */
enum class StepState { DONE, CURRENT, LATER }

fun Context.stepDot(state: StepState, number: Int?, sizeDp: Int): FrameLayout = FrameLayout(this).apply {
    when (state) {
        StepState.DONE -> {
            background = circle(Palette.MINT)
            addView(icon(R.drawable.ic_check, Palette.INK), FrameLayout.LayoutParams(dp(sizeDp * 0.55), dp(sizeDp * 0.55), Gravity.CENTER))
        }
        StepState.CURRENT -> {
            background = circle(0x00000000, Palette.AMBER, dp(2))
            if (number != null) addView(text("$number", TextStyle.CAPTION_BOLD, Palette.AMBER), FrameLayout.LayoutParams(WRAP, WRAP, Gravity.CENTER))
            else addView(View(context).apply { background = circle(Palette.AMBER) }, FrameLayout.LayoutParams(dp(8), dp(8), Gravity.CENTER))
        }
        StepState.LATER -> {
            background = circle(0x00000000, Palette.OUTLINE, dp(2))
            if (number != null) addView(text("$number", TextStyle.CAPTION_BOLD, Palette.FAINT), FrameLayout.LayoutParams(WRAP, WRAP, Gravity.CENTER))
        }
    }
}

/** Formats milliseconds as m:ss (e.g. 9:32). */
fun mmss(ms: Long): String {
    val total = (ms / 1000).coerceAtLeast(0)
    return "%d:%02d".format(total / 60, total % 60)
}
