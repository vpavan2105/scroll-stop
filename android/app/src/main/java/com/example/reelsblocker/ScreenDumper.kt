package com.example.reelsblocker

import android.content.Context
import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import java.io.File

/**
 * Debug helper: writes Instagram's current screen structure (the tree of views)
 * to a text file, so we can see the real view IDs when Instagram changes them.
 *
 * The dump can contain text that is on screen (names, captions, messages),
 * so only capture it on a Reels / tab screen and read it before sharing.
 */
object ScreenDumper {

    private const val FILE_NAME = "instagram_screen_dump.txt"
    private const val MAX_NODES = 3000

    /** The most recent capture (used by "Share last capture"). */
    fun file(context: Context) = File(context.filesDir, FILE_NAME)

    /**
     * Dumps every window on screen (main screen, pop-up menus, keyboard...).
     * Saves "instagram_screen_dump.txt" (latest) plus a numbered copy
     * "capture_1.txt", "capture_2.txt", ... so earlier captures are kept.
     */
    fun dump(context: Context, roots: List<AccessibilityNodeInfo>): File {
        val sb = StringBuilder()
        roots.forEachIndexed { i, root ->
            sb.append("===== window ").append(i).append(" package=").append(root.packageName).append('\n')
            appendTree(sb, root)
        }
        val text = sb.toString()
        val latest = file(context)
        latest.writeText(text)
        var n = 1
        while (File(context.filesDir, "capture_$n.txt").exists()) n++
        File(context.filesDir, "capture_$n.txt").writeText(text)
        return latest
    }

    private fun appendTree(sb: StringBuilder, root: AccessibilityNodeInfo) {
        var count = 0
        fun walk(node: AccessibilityNodeInfo?, depth: Int) {
            if (node == null || count >= MAX_NODES) return
            count++
            val r = Rect().also { node.getBoundsInScreen(it) }
            sb.append("  ".repeat(depth))
                .append(node.className?.toString()?.substringAfterLast('.') ?: "?")
            node.viewIdResourceName?.let { sb.append(" id=").append(it.substringAfter(":id/")) }
            node.contentDescription?.let { sb.append(" desc=\"").append(it).append('"') }
            node.text?.let { sb.append(" text=\"").append(it.toString().take(40)).append('"') }
            sb.append(" bounds=").append(r.toShortString())
            if (node.isSelected) sb.append(" SELECTED")
            if (node.isClickable) sb.append(" clickable")
            if (node.isScrollable) sb.append(" scrollable")
            if (!node.isVisibleToUser) sb.append(" hidden")
            sb.append('\n')
            for (i in 0 until node.childCount) walk(node.getChild(i), depth + 1)
        }
        walk(root, 0)
    }
}
