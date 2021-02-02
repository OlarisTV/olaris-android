package tv.olaris.android.util

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.view.Surface
import android.view.View
import android.view.WindowManager

// Copied from Jellyfin codebase at Git Commit 122448f
// https://github.com/jellyfin/jellyfin-android/blob/122448fadff259b9573382f100605f1ec99d6411/app/src/main/java/org/jellyfin/mobile/utils/ActivityExtensions.kt
const val STABLE_LAYOUT_FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

const val FULLSCREEN_FLAGS = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
        View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

//fun Activity.isFullscreen() = window.decorView.systemUiVisibility.hasFlag(FULLSCREEN_FLAGS)

fun Activity.enableFullscreen() {
    this.actionBar?.hide()

    window.apply {
        decorView.systemUiVisibility = FULLSCREEN_FLAGS
        addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    }
}

fun Activity.disableFullscreen(keepStableLayout: Boolean = false) {
    this.actionBar?.show()
    window.apply {
        decorView.systemUiVisibility = if (keepStableLayout) STABLE_LAYOUT_FLAGS else 0
        clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun Activity.lockOrientation() {
    val display = windowManager.defaultDisplay
    val size = Point().also(display::getSize)
    val height = size.y
    val width = size.x
    requestedOrientation = when (display.rotation) {
        Surface.ROTATION_90 -> if (width > height) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        Surface.ROTATION_180 -> if (height > width) ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        Surface.ROTATION_270 -> if (width > height) ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else -> if (height > width) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}