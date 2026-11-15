package com.example.reelsblocker

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Recognises the Settings pages where ScrollStop should show the
 * "This app isn't controlling you" message and the 5-minute unlock:
 *
 *   - "Usage access" for ScrollStop
 *   - "Display over other apps" for ScrollStop
 *
 * These two permissions keep Instagram and YouTube blocked while ScrollStop is
 * switched off, so removing them is the real escape hatch. Guarding them is
 * best-effort: it only works while the accessibility service is on (it draws
 * the cover), and the wording below is English only, so some phones
 * (Xiaomi/Oppo, other languages) may not be recognised.
 *
 * The Accessibility on/off switch is deliberately NOT guarded any more — you can
 * switch ScrollStop off at any time, e.g. for banking.
 */
class SettingsGuard(private val service: AccessibilityService) {

    companion object {
        private val GUARDED_PACKAGES = listOf("settings", "securitycenter", "safecenter")

        fun isGuardedPackage(pkg: String?) =
            pkg != null && GUARDED_PACKAGES.any { pkg.contains(it, ignoreCase = true) }
    }

    /** True if [root] is the Usage-access or Display-over-other-apps page for ScrollStop. */
    fun isGuardedPage(root: AccessibilityNodeInfo): Boolean {
        val pkg = root.packageName?.toString() ?: return false
        if (pkg == service.packageName || !isGuardedPackage(pkg)) return false

        val name = service.getString(R.string.app_name).lowercase()        // "scrollstop"
        val texts = NodeSearch.allTexts(root).map { it.lowercase() }
        if (texts.none { it.contains(name) }) return false                 // our app must be on the page

        val usageAccess = texts.any {
            it.contains("permit usage access") || it.contains("allow usage access")
        }
        val displayOverApps = texts.any {
            it.contains("allow display over other apps") ||
                it.contains("display over other apps") ||
                it.contains("appear on top") ||
                it.contains("draw over other apps") ||
                it.contains("drawing over other apps")
        }
        return usageAccess || displayOverApps
    }
}
