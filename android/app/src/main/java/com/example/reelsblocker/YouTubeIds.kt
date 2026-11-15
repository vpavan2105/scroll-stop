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
     * true  = the version you share yourself: switching protection off needs the 10-minute unlock.
     * false = the Play Store version: Google's rules don't allow blocking the off switch,
     *         so the message appears but you can continue.
     */
    const val STRICT_LOCK = true
}
