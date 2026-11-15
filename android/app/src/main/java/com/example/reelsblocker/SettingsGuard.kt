package com.example.reelsblocker

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Recognises the Settings screen that could switch ScrollStop off:
 * our page in Settings > Accessibility (it shows our description text, or a
 * switch called "Use ScrollStop") and the "Stop ScrollStop?" dialog.
 *
 * It only RECOGNISES the page. What happens next (the "This app isn't
 * controlling you" message, and whether you can continue) is decided by the
 * service, using [ProtectionLock] and [Edition.STRICT_LOCK].
 *
 * Uninstalling is deliberately not covered: App info and the uninstall
 * dialog stay usable. Other Settings pages, including the main
 * Accessibility list, stay usable too. English Settings only.
 */
class SettingsGuard(private val service: AccessibilityService) {

    companion object {
        private val GUARDED_PACKAGES = listOf("settings", "securitycenter", "safecenter")

        fun isGuardedPackage(pkg: String?) =
            pkg != null && GUARDED_PACKAGES.any { pkg.contains(it, ignoreCase = true) }
    }

    /** True if [root] is the page (or dialog) that switches our service off. */
    fun isSwitchOffPage(root: AccessibilityNodeInfo): Boolean {
        val pkg = root.packageName?.toString() ?: return false
        if (pkg == service.packageName || !isGuardedPackage(pkg)) return false

        val name = service.getString(R.string.app_name).lowercase()               // "reels blocker"
        val ourDescription = service.getString(R.string.accessibility_description)
            .take(40).lowercase()                                               // first words are enough
        val texts = NodeSearch.allTexts(root).map { it.lowercase() }

        return texts.any {
            it.contains(ourDescription) || it.contains("use $name") ||
                it.contains("stop $name") || it.contains("turn off $name")
        }
    }
}
