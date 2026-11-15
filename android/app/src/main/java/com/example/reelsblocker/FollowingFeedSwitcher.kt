package com.example.reelsblocker

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.os.SystemClock
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Opens Instagram's "Following" feed (only accounts you follow, newest first,
 * no suggested posts or suggested reels).
 *
 * Instagram already has this feed, but it resets to "For you" every time the
 * app opens. So we do what you would do by hand:
 *
 *   step 1: tap the "Instagram" / "For you" title at the top of Home
 *   step 2: in the small menu that opens, tap "Following"
 *
 * When it happens:
 *  - automatically ONCE each time you open Instagram
 *  - again whenever you tap the "feed hidden" panel ([requestSwitch])
 * So pressing Back from Following leaves you on Home with the bottom bar
 * (the suggested posts there are covered by the service).
 */
class FollowingFeedSwitcher(private val service: AccessibilityService) {

    companion object {
        private const val MENU_WAIT_MS = 2_000L        // how long to wait for the menu
        private const val RETRY_GAP_MS = 3_000L        // pause between automatic tries
        private const val MAX_AUTO_TRIES = 3           // give up if Instagram changed
        private const val NEW_VISIT_AFTER_MS = 30_000L // away this long = a new visit

        // Title at the top of the normal Home feed (different app versions)
        private val HOME_TITLE_LABELS = listOf("Instagram", "For you")
        private val FOLLOWING_LABELS = listOf("Following")
    }

    private var waitingForMenuUntil = 0L
    private var lastAttemptAt = 0L
    private var autoTries = 0
    private var autoSwitchDone = false
    private var switchRequested = false
    private var leftAt = 0L

    /** Called while Instagram is not on screen. */
    fun onLeftInstagram() {
        if (leftAt == 0L) leftAt = SystemClock.uptimeMillis()
        waitingForMenuUntil = 0L
    }

    /** The user tapped the "feed hidden" panel. */
    fun requestSwitch() {
        switchRequested = true
    }

    /**
     * Look at the screen; tap if needed.
     * Returns true when it tapped something (the screen is about to change).
     */
    fun check(root: AccessibilityNodeInfo): Boolean {
        val now = SystemClock.uptimeMillis()

        // Back in Instagram after a while? Then this is a new visit: auto-switch again.
        if (leftAt != 0L) {
            if (now - leftAt > NEW_VISIT_AFTER_MS) {
                autoSwitchDone = false
                autoTries = 0
            }
            leftAt = 0L
        }

        // Step 2: the menu should be open -> tap "Following"
        if (now < waitingForMenuUntil) {
            val item = findFollowingMenuItem() ?: return false   // not there yet, keep waiting
            waitingForMenuUntil = 0L
            val ok = click(item)
            if (ok) autoSwitchDone = true
            return ok
        }

        // Step 1: should we switch now?
        val wantAuto = !autoSwitchDone && autoTries < MAX_AUTO_TRIES &&
            now - lastAttemptAt >= RETRY_GAP_MS
        if (!switchRequested && !wantAuto) return false

        val title = findHomeTitle(root) ?: return false
        if (!switchRequested) autoTries++
        switchRequested = false
        lastAttemptAt = now
        if (click(title)) {
            waitingForMenuUntil = now + MENU_WAIT_MS
            return true
        }
        return false
    }

    /**
     * The "Instagram" logo / "For you" title in the top bar of Home.
     * If this is on screen, we are on the normal (suggested) Home feed.
     */
    fun findHomeTitle(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        val topBar = screenHeight() * 0.15
        return NodeSearch.findFirst(root) { n ->
            n.isVisibleToUser && NodeSearch.boundsOf(n).bottom <= topBar &&
                NodeSearch.labelMatches(n, HOME_TITLE_LABELS)
        }
    }

    /**
     * The "Following" choice in the menu. The menu may be its own little window,
     * so we search every Instagram window on screen, not just the main one.
     */
    private fun findFollowingMenuItem(): AccessibilityNodeInfo? {
        val menuArea = screenHeight() * 0.6
        val roots = service.windows.mapNotNull { it.root } + listOfNotNull(service.rootInActiveWindow)
        for (root in roots) {
            if (root.packageName != InstagramIds.PACKAGE) continue
            NodeSearch.findFirst(root) { n ->
                n.isVisibleToUser && NodeSearch.boundsOf(n).bottom <= menuArea &&
                    NodeSearch.labelMatches(n, FOLLOWING_LABELS)
            }?.let { return it }
        }
        return null
    }

    /** Tap the node, or the nearest parent that can be tapped. */
    private fun click(node: AccessibilityNodeInfo): Boolean {
        var n: AccessibilityNodeInfo? = node
        while (n != null && !n.isClickable) n = n.parent
        return n?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true
    }

    private fun screenHeight() = service.resources.displayMetrics.heightPixels
}

/** Small shared helpers for searching the screen's view tree. */
object NodeSearch {

    /** Breadth-first search: checks the top of the tree first, then goes deeper. */
    fun findFirst(
        root: AccessibilityNodeInfo,
        match: (AccessibilityNodeInfo) -> Boolean,
    ): AccessibilityNodeInfo? {
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        var visited = 0
        while (queue.isNotEmpty() && visited < 2000) {
            val node = queue.removeFirst()
            visited++
            if (match(node)) return node
            for (i in 0 until node.childCount) node.getChild(i)?.let { queue.add(it) }
        }
        return null
    }

    /** Visit every node (up to 2000) and call [action] on each. */
    fun forEach(root: AccessibilityNodeInfo, action: (AccessibilityNodeInfo) -> Unit) {
        findFirst(root) { action(it); false }
    }

    /** All text and accessibility labels on the screen. */
    fun allTexts(root: AccessibilityNodeInfo): List<String> {
        val out = mutableListOf<String>()
        forEach(root) { n ->
            n.text?.let { out.add(it.toString().trim()) }
            n.contentDescription?.let { out.add(it.toString().trim()) }
        }
        return out
    }

    fun boundsOf(node: AccessibilityNodeInfo) = Rect().also { node.getBoundsInScreen(it) }

    /**
     * True if the node's text or accessibility label is one of [labels],
     * or starts with it followed by a space/comma (e.g. "For you, feed options").
     * "instagram's story" does NOT match "Instagram" (next char is an apostrophe).
     */
    fun labelMatches(node: AccessibilityNodeInfo, labels: List<String>): Boolean {
        val words = listOfNotNull(node.text?.toString(), node.contentDescription?.toString())
        return words.any { raw ->
            val w = raw.trim().lowercase()
            labels.any { label ->
                val l = label.lowercase()
                w == l || w.startsWith("$l,") || w.startsWith("$l ") || w.startsWith("$l\n")
            }
        }
    }
}
