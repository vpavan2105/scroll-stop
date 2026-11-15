package com.example.reelsblocker

/**
 * Names of Instagram's own views (buttons, screens) as Android reports them.
 *
 * Instagram can rename these in an update. If something stops working, use the
 * "Capture Instagram screen" button in the app, look at the saved capture and
 * update the names here.
 */
object InstagramIds {
    const val PACKAGE = "com.instagram.android"

    private fun id(name: String) = "$PACKAGE:id/$name"

    // Bottom navigation bar buttons
    val HOME_TAB = id("feed_tab")
    val REELS_TAB = id("clips_tab")          // Instagram calls Reels "clips" internally
    val EXPLORE_TAB = id("search_tab")       // the magnifying-glass tab (Search & Explore)
    val DIRECT_TAB = id("direct_tab")        // messages (in newer versions)
    val PROFILE_TAB = id("profile_tab")

    /** Tabs that are NOT Home: if one of these is selected, we are not on the Home feed. */
    val OTHER_TABS = listOf(REELS_TAB, EXPLORE_TAB, DIRECT_TAB, PROFILE_TAB)

    // The full-screen Reels player (vertical swipe pager).
    // Careful: "reel" in Instagram's IDs usually means STORIES. Don't add those.
    val REELS_PLAYER = listOf(
        id("clips_viewer_view_pager"),
        id("clips_viewer_container"),
    )

    // Back-up plan if the IDs change: the button's accessibility label (English UI).
    val HOME_TAB_LABELS = listOf("Home")
    val REELS_TAB_LABELS = listOf("Reels")
    val EXPLORE_TAB_LABELS = listOf("Search and explore", "Search and Explore", "Explore", "Search")
}
