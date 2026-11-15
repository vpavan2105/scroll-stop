package com.example.reelsblocker

/**
 * Names of YouTube's own views, like [InstagramIds] for Instagram.
 *
 * Careful: YouTube calls Shorts "reels" internally (the opposite of
 * Instagram, which calls Reels "clips"). These names can change after a
 * YouTube update; "Re-learn" in the app fixes that without a rebuild.
 */
object YouTubeIds {
    const val PACKAGE = "com.google.android.youtube"

    private fun id(name: String) = "$PACKAGE:id/$name"

    // The full-screen Shorts player (vertical swipe pager)
    val SHORTS_PLAYER = listOf(
        id("reel_recycler"),
        id("reel_player_page_container"),
        id("reel_watch_player"),
    )

    // YouTube's bottom bar buttons have no stable IDs, so we find them by label (English UI).
    val SHORTS_TAB_LABELS = listOf("Shorts")
    val HOME_TAB_LABELS = listOf("Home")
}

/** Which kind of build this is. */
object Edition {
    /**
     * true  = the version you share yourself: the 5-minute unlock guards removing a
     *         required permission (Usage access / Display over other apps) and raising a limit.
     *         Switching protection off for banking is always allowed.
     * false = a relaxed build: the message appears but you can continue without the wait.
     */
    const val STRICT_LOCK = true
}
