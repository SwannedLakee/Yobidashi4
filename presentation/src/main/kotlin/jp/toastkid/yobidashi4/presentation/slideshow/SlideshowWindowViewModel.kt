package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState

class SlideshowWindowViewModel {

    private val windowState = WindowState(placement = WindowPlacement.Maximized)

    fun windowState() = windowState

    fun toggleFullscreen() {
        windowState.placement =
            if (windowState.placement == WindowPlacement.Maximized) WindowPlacement.Floating
            else WindowPlacement.Maximized
    }

    fun closeFullscreen() {
        windowState.placement = WindowPlacement.Floating
    }

    fun isFloatingWindow() = windowState.placement == WindowPlacement.Floating

    fun onEscapeKeyReleased(onCloseWindow: () -> Unit) {
        if (isFloatingWindow()) {
            onCloseWindow()
            return
        }

        closeFullscreen()
    }

    fun windowVisible() = true

}