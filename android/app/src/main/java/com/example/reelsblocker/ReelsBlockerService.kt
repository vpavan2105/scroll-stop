package com.example.reelsblocker

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.example.reelsblocker.ui.UnblockMode
import com.example.reelsblocker.ui.UnlockActivity
import com.example.reelsblocker.ui.limitReachedPanel
import com.example.reelsblocker.ui.unblockMessagePanel

/**
 * The brain of the app. Android sends this service an "event" every time
 * something changes on screen. After each burst of events we look at the
 * screen and, depending on which app is in front:
 *
 *  Instagram
 *   1. cover the Reels tab and the Explore tab ([TabCover])
 *   2. if you still land on the Reels or Explore tab, switch to Home
 *   3. full-screen reels (from chats / posts / profiles): daily limit ([PlayerLimiter])
 *   4. "For you" Home feed: daily limit of suggested posts, then the feed is covered
 *   5. when you open Instagram, jump to "Following" once ([FollowingFeedSwitcher])
 *  YouTube
 *   6. cover the Shorts tab; Shorts opened from the Home feed: daily limit
 *  Settings
 *   7. on the page that switches us off, show "This app isn't controlling you" ([SettingsGuard])
 *
 * Plus: health check ([InstagramHealthCheck]), teach mode ([LearnedIds]),
 * and a safety net so an unexpected error never crashes the service.
 */
class ReelsBlockerService : AccessibilityService() {

    companion object {
        /** Lets the settings screens talk to the running service (same app process). */
        @Volatile
        var instance: ReelsBlockerService? = null
            private set

        private const val REFRESH_DELAY_MS = 120L    // wait for a burst of events to finish
        private const val PRESENCE_CHECK_MS = 600L   // how often we check you're still in Instagram/YouTube
        private const val SYSTEM_UI = "com.android.systemui"
        private const val ACTION_COOLDOWN_MS = 800L  // don't press Back twice in a row
        private const val SWIPE_SETTLE_MS = 450L     // a feed scroll is "done" after this much quiet
        private const val TEACH_WINDOW_MS = 30_000L  // teach mode listens this long
        private const val CONTINUE_WINDOW_MS = 2 * 60_000L   // after "Continue to switch off"

        /** Scrolling this much of the screen height on the feed = one post seen. */
        private const val POST_HEIGHT_FRACTION = 0.7

        // Safety net: this many errors within a minute -> notification
        private const val ERROR_BURST = 20
        private const val ERROR_WINDOW_MS = 60_000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var feedCounter: DailyCounter
    private lateinit var reels: PlayerLimiter
    private lateinit var shorts: PlayerLimiter
    private lateinit var reelsCover: TabCover
    private lateinit var exploreCover: TabCover
    private lateinit var feedCover: TabCover
    private lateinit var shortsCover: TabCover
    private lateinit var guardCover: TabCover
    private lateinit var followingSwitcher: FollowingFeedSwitcher
    private lateinit var learned: LearnedIds
    private lateinit var lock: ProtectionLock
    private lateinit var guard: SettingsGuard
    private lateinit var health: InstagramHealthCheck

    private var refreshPending = false
    private var lastActionAt = 0L

    // We only receive events from Instagram, YouTube and Settings (see
    // accessibility_service_config.xml), so we never hear about the home screen
    // or other apps. While you're in a watched app, a cheap check notices when
    // you leave, so the covers disappear and the "once per visit" logic resets.
    private var inWatchedApp = false
    private var presenceScheduled = false
    private val presenceCheck = Runnable {
        presenceScheduled = false
        safely("presence") {
            val pkg = rootInActiveWindow?.packageName?.toString()
            val stillWatched = pkg == InstagramIds.PACKAGE || pkg == YouTubeIds.PACKAGE
            // No window (mid-transition) or the notification shade: not a real
            // exit, so don't reset the "once per visit" Following switch.
            val inBetween = pkg == null || pkg == SYSTEM_UI
            if (!inBetween && (!stillWatched || !inWatchedApp)) evaluateScreen()
        }
        keepWatching()
    }

    // State of the "For you" Home feed
    private var onForYouFeed = false
    private var feedScrolledPx = 0

    // State of the switch-off message
    private var guardMode = UnblockMode.LOCKED
    private var guardDismissedUntil = 0L

    // Teach mode
    private var teachTarget: TeachTarget? = null
    private var teachUntil = 0L

    // Safety net
    private val errorTimes = ArrayDeque<Long>()

    // ---------------------------------------------------------------- lifecycle

    override fun onServiceConnected() = safely("connect") {
        feedCounter = DailyCounter.feedPosts(this)
        reels = PlayerLimiter(
            DailyCounter.reels(this), "reel", "reels", handler,
            goBack = { goBack() }, toast = { toast(it) }, onError = { w, t -> recordError(w, t) },
        )
        shorts = PlayerLimiter(
            DailyCounter.shorts(this), "Short", "Shorts", handler,
            goBack = { goBack() }, toast = { toast(it) }, onError = { w, t -> recordError(w, t) },
        )
        reelsCover = TabCover(this)
        exploreCover = TabCover(this)
        shortsCover = TabCover(this)
        feedCover = TabCover(
            this,
            content = { ctx -> ctx.limitReachedPanel(feedCounter.limit) },   // see ui/LimitPanel.kt
            onTap = { safely("feed cover tap") { onFeedCoverTapped() } },
        )
        guardCover = TabCover(
            this,
            content = { ctx ->                                               // see ui/UnblockPanel.kt
                ctx.unblockMessagePanel(
                    guardMode,
                    onKeep = { safely("keep") { guardCover.hide(); performGlobalAction(GLOBAL_ACTION_BACK) } },
                    onContinue = { safely("continue") { guardDismissedUntil = SystemClock.uptimeMillis() + CONTINUE_WINDOW_MS; guardCover.hide() } },
                    onOpenApp = { safely("open app") { guardCover.hide(); openUnlockScreen() } },
                )
            },
        )
        followingSwitcher = FollowingFeedSwitcher(this)
        learned = LearnedIds(this)
        lock = ProtectionLock(this)
        guard = SettingsGuard(this)
        health = InstagramHealthCheck(this)
        instance = this

        Notifier.cancel(this, Notifier.ID_OFF)       // we're running again
        WatchdogJobService.schedule(this)            // checks on us every ~15 min
        WatcherService.stop(this)                    // we're on now; the off-guard isn't needed
    }

    override fun onInterrupt() { /* required by Android; nothing to do */ }

    /** Called when the service is switched off in Settings. */
    override fun onUnbind(intent: Intent?): Boolean {
        if (OffGuard.active(this)) {
            // Off-guard set up: block Instagram/YouTube and show its own reminder instead.
            WatcherService.start(this)
        } else {
            Notifier.showOff(this, "Protection was switched off. Tap to turn it back on.")
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        safely("destroy") { hideAllCovers() }
        handler.removeCallbacksAndMessages(null)
        instance = null
        super.onDestroy()
    }

    // ---------------------------------------------------------------- events

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = safely("event") {
        if (event == null || instance == null) return@safely
        val pkg = event.packageName?.toString()
        val watched = pkg == InstagramIds.PACKAGE || pkg == YouTubeIds.PACKAGE
        val guarded = SettingsGuard.isGuardedPackage(pkg)

        // Other apps: we only care if we must clean up (covers shown / player open).
        if (!watched && !guarded && !anyCoverShowing() && !reels.isOpen && !shorts.isOpen &&
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) return@safely

        if (teachTarget?.packageName == pkg) onTeachEvent(event)

        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            val source = event.source
            when {
                source == null -> {}
                pkg == InstagramIds.PACKAGE && reels.isOpen ->
                    if (isFullScreenScroller(source, reelsPlayerIds())) reels.onScrolled(event)
                pkg == InstagramIds.PACKAGE && onForYouFeed ->
                    if (isFullScreenScroller(source, emptyList())) onFeedScrolled(event)
                pkg == YouTubeIds.PACKAGE && shorts.isOpen ->
                    if (isFullScreenScroller(source, shortsPlayerIds())) shorts.onScrolled(event)
            }
        }

        scheduleRefresh()
    }

    /** Many events arrive together; handle them once, shortly after the burst. */
    private fun scheduleRefresh() {
        if (refreshPending) return
        refreshPending = true
        handler.postDelayed({
            refreshPending = false
            safely("evaluate") { evaluateScreen() }
            keepWatching()
        }, REFRESH_DELAY_MS)
    }

    /** Keep the presence check running while you're in a watched app or a cover is up. */
    private fun keepWatching() {
        if (presenceScheduled || !(inWatchedApp || anyCoverShowing())) return
        presenceScheduled = true
        handler.postDelayed(presenceCheck, PRESENCE_CHECK_MS)
    }

    // ---------------------------------------------------------------- main logic

    private fun evaluateScreen() {
        // Teach mode ran out of time?
        if (teachTarget != null && SystemClock.uptimeMillis() > teachUntil) {
            toast("Teach mode ended: nothing was learned")
            teachTarget = null
        }

        val root = rootInActiveWindow
        val pkg = root?.packageName?.toString()
        inWatchedApp = pkg == InstagramIds.PACKAGE || pkg == YouTubeIds.PACKAGE
        if (pkg != InstagramIds.PACKAGE) leaveInstagram()
        if (pkg != YouTubeIds.PACKAGE) leaveYouTube()
        if (root == null) { guardCover.hide(); return }

        when (pkg) {
            InstagramIds.PACKAGE -> { guardCover.hide(); evaluateInstagram(root) }
            YouTubeIds.PACKAGE -> { guardCover.hide(); evaluateYouTube(root) }
            else -> evaluateSettings(root)
        }
    }

    private fun evaluateInstagram(root: AccessibilityNodeInfo) {
        // Find the tabs.
        val reelsTab = findTab(
            root,
            learned.ids(TeachTarget.REELS_TAB, listOf(InstagramIds.REELS_TAB)),
            learned.labels(TeachTarget.REELS_TAB, InstagramIds.REELS_TAB_LABELS),
        )
        val exploreTab = findTab(
            root,
            learned.ids(TeachTarget.EXPLORE_TAB, listOf(InstagramIds.EXPLORE_TAB)),
            learned.labels(TeachTarget.EXPLORE_TAB, InstagramIds.EXPLORE_TAB_LABELS),
        )
        val homeTab = findTab(root, listOf(InstagramIds.HOME_TAB), InstagramIds.HOME_TAB_LABELS)
        health.onInstagramScreen(root, reelsTab != null, exploreTab != null)

        // 1. Hide a tab only ONCE today's limit is used up. Below the limit you can
        //    still open Reels and Explore (so Explore search keeps working).
        val reelsOverLimit = reels.limitReached()
        val exploreOverLimit = feedCounter.limitReached()
        if (reelsOverLimit) coverNode(reelsCover, reelsTab) else reelsCover.hide()
        if (exploreOverLimit) coverNode(exploreCover, exploreTab) else exploreCover.hide()

        // 2. On the Reels or Explore tab AFTER the limit? Bounce Home.
        val onReels = reelsTab?.isSelected == true
        val onExplore = exploreTab?.isSelected == true
        if ((onReels && reelsOverLimit) || (onExplore && exploreOverLimit)) {
            reels.close()
            goHome(homeTab)
            return
        }

        // 3. Reels player (from the tab, a chat, a post or a profile): count each reel,
        //    and PlayerLimiter closes it once the daily limit is reached.
        val playerVisible = anyVisible(root, reelsPlayerIds())
        reels.update(playerVisible)

        // 4. The "For you" Home feed: daily limit of suggested posts, then cover it.
        val onReelsOrExplore = onReels || onExplore
        val homeTitle = if (playerVisible || onReelsOrExplore) null else followingSwitcher.findHomeTitle(root)
        onForYouFeed = when {
            playerVisible || onReelsOrExplore -> false
            homeTitle != null -> true            // the "For you" / "Instagram" title is visible
            homeTab == null -> false             // no bottom bar: Following feed, a chat, ...
            otherTabSelected(root) -> false      // you switched to Profile, Messages, ...
            else -> onForYouFeed                 // title scrolled away: still the same feed
        }
        if (onForYouFeed && homeTab != null && feedCounter.limitReached()) {
            val top = homeTitle?.let { feedTopBelow(root, boundsOf(it).bottom) } ?: 0
            val bottom = boundsOf(homeTab).top
            feedCover.showOver(Rect(0, top, screenWidth(), bottom))
        } else {
            feedCover.hide()
        }

        // 5. Just opened Instagram? Jump to "Following" once — but not while on Reels/Explore.
        if (!playerVisible && !onReelsOrExplore) followingSwitcher.check(root)
    }

    private fun evaluateYouTube(root: AccessibilityNodeInfo) {
        // 6. Hide the Shorts tab; limit Shorts opened from the Home feed
        val shortsTab = findTab(
            root,
            learned.ids(TeachTarget.SHORTS_TAB, emptyList()),
            learned.labels(TeachTarget.SHORTS_TAB, YouTubeIds.SHORTS_TAB_LABELS),
        )
        val shortsOverLimit = shorts.limitReached()
        if (shortsOverLimit) coverNode(shortsCover, shortsTab) else shortsCover.hide()
        if (shortsTab?.isSelected == true && shortsOverLimit) {
            shorts.close()
            goHome(findTab(root, emptyList(), YouTubeIds.HOME_TAB_LABELS))
            return
        }
        shorts.update(anyVisible(root, shortsPlayerIds()))
    }

    /** 7. The Settings page that switches us off: cover it with the message. */
    private fun evaluateSettings(root: AccessibilityNodeInfo) {
        if (!guard.isGuardedPage(root) || SystemClock.uptimeMillis() < guardDismissedUntil) {
            guardCover.hide()
            return
        }
        val mode = if (Edition.STRICT_LOCK && !lock.isUnlocked()) UnblockMode.LOCKED else UnblockMode.CAN_CONTINUE
        if (mode != guardMode) {
            guardCover.hide()          // rebuild the panel with the right buttons
            guardMode = mode
        }
        guardCover.showOver(fullScreen())
    }

    private fun leaveInstagram() {
        if (!::reels.isInitialized) return
        reelsCover.hide()
        exploreCover.hide()
        feedCover.hide()
        reels.close()
        onForYouFeed = false
        followingSwitcher.onLeftInstagram()
    }

    private fun leaveYouTube() {
        if (!::shorts.isInitialized) return
        shortsCover.hide()
        shorts.close()
    }

    private fun reelsPlayerIds() = learned.ids(TeachTarget.REELS_PLAYER, InstagramIds.REELS_PLAYER)

    private fun shortsPlayerIds() = learned.ids(TeachTarget.SHORTS_PLAYER, YouTubeIds.SHORTS_PLAYER)

    private fun openUnlockScreen() {
        startActivity(Intent(this, UnlockActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    // ---------------------------------------------------------------- "For you" feed

    /**
     * The feed list scrolled. We add up how far you scrolled down; every
     * [POST_HEIGHT_FRACTION] of a screen counts as one post (or inline reel).
     */
    private fun onFeedScrolled(event: AccessibilityEvent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val dy = event.scrollDeltaY
            if (dy > 0) feedScrolledPx += dy                   // only scrolling down counts
        } else {
            // Android 8: no scroll distance available -> one burst of scrolling = one post
            handler.removeCallbacks(feedBurstSettled)
            handler.postDelayed(feedBurstSettled, SWIPE_SETTLE_MS)
        }

        val postPx = (screenHeight() * POST_HEIGHT_FRACTION).toInt()
        while (feedScrolledPx >= postPx) {
            feedScrolledPx -= postPx
            onFeedPostSeen()
        }
    }

    private val feedBurstSettled = Runnable { safely("feed burst") { if (onForYouFeed) onFeedPostSeen() } }

    private fun onFeedPostSeen() {
        if (feedCounter.limitReached()) { scheduleRefresh(); return }
        when (val left = feedCounter.limit - feedCounter.increment()) {
            10, 5 -> toast("$left suggested posts left today")
            1 -> toast("1 suggested post left today")
            0 -> {
                toast("That's ${feedCounter.limit} suggested posts for today")
                scheduleRefresh()                                 // show the cover now
            }
        }
    }

    /** You tapped the cover over the feed: open "Following". */
    private fun onFeedCoverTapped() {
        val root = rootInActiveWindow ?: return
        // If the title scrolled away, tapping Home scrolls the feed back to the top.
        if (followingSwitcher.findHomeTitle(root) == null) {
            clickNode(findTab(root, listOf(InstagramIds.HOME_TAB), InstagramIds.HOME_TAB_LABELS))
        }
        followingSwitcher.requestSwitch()
        scheduleRefresh()
    }

    /**
     * Where the feed starts: just below the Stories row if we can find it
     * (so Stories stay usable), otherwise just below the title bar.
     */
    private fun feedTopBelow(root: AccessibilityNodeInfo, titleBottom: Int): Int {
        val h = screenHeight()
        val stories = NodeSearch.findFirst(root) { n ->
            val b = boundsOf(n)
            n.isScrollable && n.isVisibleToUser &&
                b.top >= titleBottom - 10 && b.top <= titleBottom + h * 0.05 &&
                b.height() < h * 0.25 && b.width() > screenWidth() * 0.8
        }
        return stories?.let { boundsOf(it).bottom } ?: titleBottom
    }

    private fun otherTabSelected(root: AccessibilityNodeInfo): Boolean =
        InstagramIds.OTHER_TABS.any { id ->
            root.findAccessibilityNodeInfosByViewId(id).any { it.isSelected }
        }

    // ---------------------------------------------------------------- teach mode

    /** Called from the settings screens. Listens for the next tap / swipe in that app. */
    fun startTeaching(target: TeachTarget) {
        teachTarget = target
        teachUntil = SystemClock.uptimeMillis() + TEACH_WINDOW_MS
    }

    private fun onTeachEvent(event: AccessibilityEvent) {
        val target = teachTarget ?: return
        val source = event.source ?: return
        if (target.isPlayer) {
            if (event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) return
            if (!source.isScrollable || boundsOf(source).height() < screenHeight() * 0.85) return
            finishTeaching(target, source.viewIdResourceName ?: return)
        } else {
            if (event.eventType != AccessibilityEvent.TYPE_VIEW_CLICKED) return
            finishTeaching(target, idOrLabel(source) ?: return)
        }
    }

    /** The tapped view's ID (or a parent's, up to 3 levels), else its label. */
    private fun idOrLabel(node: AccessibilityNodeInfo): String? {
        var n: AccessibilityNodeInfo? = node
        repeat(4) {
            n?.viewIdResourceName?.let { return it }
            n = n?.parent
        }
        return node.contentDescription?.toString()?.let { LearnedIds.LABEL_PREFIX + it }
    }

    private fun finishTeaching(target: TeachTarget, value: String) {
        learned.save(target, value)
        teachTarget = null
        toast("Learned the ${target.title}: ${value.substringAfter(":id/")}")
    }

    // ---------------------------------------------------------------- finding things

    private fun anyVisible(root: AccessibilityNodeInfo, ids: List<String>): Boolean =
        ids.any { id -> root.findAccessibilityNodeInfosByViewId(id).any { it.isVisibleToUser } }

    /** A big scrollable list/pager that fills most of the screen (not the Stories row). */
    private fun isFullScreenScroller(node: AccessibilityNodeInfo, ids: List<String>): Boolean {
        if (node.viewIdResourceName?.let { it in ids } == true) return true
        return node.isScrollable && boundsOf(node).height() > screenHeight() * 0.5
    }

    /** Find a bottom-bar tab by one of its view IDs, or by its label as a back-up. */
    private fun findTab(
        root: AccessibilityNodeInfo,
        viewIds: List<String>,
        labels: List<String>,
    ): AccessibilityNodeInfo? {
        for (id in viewIds) {
            root.findAccessibilityNodeInfosByViewId(id)
                .firstOrNull { it.isVisibleToUser }
                ?.let { return it }
        }
        val bottomArea = (screenHeight() * 0.8).toInt()
        return NodeSearch.findFirst(root) { n ->
            n.isVisibleToUser && boundsOf(n).top >= bottomArea && NodeSearch.labelMatches(n, labels)
        }
    }

    // ---------------------------------------------------------------- actions

    private fun coverNode(cover: TabCover, node: AccessibilityNodeInfo?) {
        if (node == null) cover.hide() else cover.showOver(boundsOf(node))
    }

    private fun anyCoverShowing() =
        ::reelsCover.isInitialized &&
            (reelsCover.isShowing || exploreCover.isShowing || feedCover.isShowing ||
                shortsCover.isShowing || guardCover.isShowing)

    private fun hideAllCovers() {
        if (!::reelsCover.isInitialized) return
        listOf(reelsCover, exploreCover, feedCover, shortsCover, guardCover).forEach { it.hide() }
    }

    /** Tap the app's Home tab; if we can't find it, press Back. */
    private fun goHome(homeTab: AccessibilityNodeInfo?) {
        if (!cooldownOver()) return
        if (!clickNode(homeTab)) performGlobalAction(GLOBAL_ACTION_BACK)
    }

    /** Tap the node, or the nearest parent that can be tapped. */
    private fun clickNode(start: AccessibilityNodeInfo?): Boolean {
        var node = start
        while (node != null && !node.isClickable) node = node.parent
        return node?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true
    }

    private fun goBack() {
        if (!cooldownOver()) return
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    private fun cooldownOver(): Boolean {
        val now = SystemClock.uptimeMillis()
        if (now - lastActionAt < ACTION_COOLDOWN_MS) return false
        lastActionAt = now
        return true
    }

    // ---------------------------------------------------------------- safety net

    /**
     * Runs [block]; if anything inside throws, we note the error and carry on
     * instead of crashing (a crash makes Android switch the service off).
     */
    private inline fun safely(where: String, block: () -> Unit) {
        try {
            block()
        } catch (t: Throwable) {
            recordError(where, t)
        }
    }

    private fun recordError(where: String, t: Throwable) {
        ErrorLog.record(this, "$where: ${t.javaClass.simpleName}: ${t.message}")
        val now = SystemClock.uptimeMillis()
        errorTimes.addLast(now)
        while (errorTimes.isNotEmpty() && now - errorTimes.first() > ERROR_WINDOW_MS) errorTimes.removeFirst()
        if (errorTimes.size >= ERROR_BURST) {
            errorTimes.clear()
            Notifier.showErrors(this, "Something keeps going wrong (${t.javaClass.simpleName}). " +
                "Open the app to see the last error.")
        }
    }

    // ---------------------------------------------------------------- helpers

    private fun boundsOf(node: AccessibilityNodeInfo) = NodeSearch.boundsOf(node)

    private fun screenHeight() = resources.displayMetrics.heightPixels

    private fun screenWidth() = resources.displayMetrics.widthPixels

    /** The whole display, including the status and navigation bars. */
    private fun fullScreen(): Rect {
        val wm = getSystemService(WindowManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) return Rect(wm.maximumWindowMetrics.bounds)
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION") wm.defaultDisplay.getRealMetrics(metrics)
        return Rect(0, 0, metrics.widthPixels, metrics.heightPixels)
    }

    private fun toast(message: String) =
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

    // ---------------------------------------------------------------- debug

    /** Called from the settings screens: save the screen layout after [delayMs]. */
    fun captureScreenAfter(delayMs: Long) {
        handler.postDelayed({
            safely("capture") {
                val roots = windows.mapNotNull { it.root }.ifEmpty { listOfNotNull(rootInActiveWindow) }
                if (roots.isEmpty()) {
                    toast("Couldn't read the screen")
                } else {
                    ScreenDumper.dump(this, roots)
                    toast("Screen layout saved (${roots.size} windows)")
                }
            }
        }, delayMs)
    }
}

/** Keeps the last error so the settings screens can show it. */
object ErrorLog {
    private const val PREFS = "error_log"

    fun record(context: android.content.Context, message: String) {
        context.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE).edit()
            .putString("last", message.take(300))
            .putLong("at", System.currentTimeMillis())
            .apply()
    }

    fun last(context: android.content.Context): String? {
        val p = context.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE)
        val msg = p.getString("last", null) ?: return null
        val at = java.text.DateFormat.getDateTimeInstance().format(java.util.Date(p.getLong("at", 0)))
        return "$at — $msg"
    }
}
