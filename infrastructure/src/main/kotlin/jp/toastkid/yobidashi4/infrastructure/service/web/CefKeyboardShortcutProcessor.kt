package jp.toastkid.yobidashi4.infrastructure.service.web

import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.infrastructure.service.web.screenshot.ScreenshotExporter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.cef.browser.CefBrowser
import org.cef.handler.CefKeyboardHandler
import org.cef.misc.EventFlags
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.event.KeyEvent

class CefKeyboardShortcutProcessor(
    private val selectedText: () -> String
) : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val pool : WebViewPool by inject()

    operator fun invoke(
        browser: CefBrowser?,
        eventType: CefKeyboardHandler.CefKeyEvent.EventType,
        modifier: Int,
        keyCode: Int
    ): Boolean {
        if (eventType != CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP) {
            return false
        }

        if (modifier == EventFlags.EVENTFLAG_CONTROL_DOWN && keyCode == KeyEvent.VK_P) {
            browser ?: return true
            browser.printToPDF("${browser.identifier}.pdf", null, null)
            return true
        }
        if (modifier == EventFlags.EVENTFLAG_CONTROL_DOWN && keyCode == KeyEvent.VK_UP) {
            browser?.executeJavaScript("window.scrollTo(0, 0);", null, 1)
            return true
        }
        if (modifier == EventFlags.EVENTFLAG_CONTROL_DOWN && keyCode == KeyEvent.VK_DOWN) {
            browser?.executeJavaScript("window.scrollTo(0, document.body.scrollHeight);", null, 1)
            return true
        }
        if (modifier == EventFlags.EVENTFLAG_CONTROL_DOWN && keyCode == KeyEvent.VK_B) {
            BookmarkInsertion()(latestUrl = browser?.url)
            return true
        }
        if (keyCode == KeyEvent.VK_F5) {
            browser?.reload()
            return true
        }
        if (modifier == EventFlags.EVENTFLAG_CONTROL_DOWN && keyCode == KeyEvent.VK_R) {
            browser?.reloadIgnoreCache()
            return true
        }
        if (modifier == EventFlags.EVENTFLAG_CONTROL_DOWN && keyCode == 187) {
            browser?.let {
                it.zoomLevel = it.zoomLevel + 0.25
            }
            return true
        }
        if (modifier == EventFlags.EVENTFLAG_CONTROL_DOWN && keyCode == 189) {
            browser?.let {
                it.zoomLevel -= 0.25
            }
            return true
        }
        if (modifier == EventFlags.EVENTFLAG_ALT_DOWN && keyCode == KeyEvent.VK_LEFT) {
            browser?.let {
                if (it.canGoBack()) {
                    it.goBack()
                }
            }
            return true
        }
        if (modifier == EventFlags.EVENTFLAG_ALT_DOWN && keyCode == KeyEvent.VK_RIGHT) {
            browser?.let {
                if (it.canGoForward()) {
                    it.goForward()
                }
            }
            return true
        }
        if (modifier == EventFlags.EVENTFLAG_SHIFT_DOWN or EventFlags.EVENTFLAG_CONTROL_DOWN
            && keyCode == KeyEvent.VK_O) {
            viewModel.webSearch(selectedText())
            return true
        }
        if (modifier == EventFlags.EVENTFLAG_ALT_DOWN or EventFlags.EVENTFLAG_CONTROL_DOWN
            && keyCode == KeyEvent.VK_O) {
            viewModel.browseUri(selectedText())
            return true
        }
        if (modifier == EventFlags.EVENTFLAG_SHIFT_DOWN
            && keyCode == KeyEvent.VK_P
            && browser != null
            ) {
            ScreenshotExporter().invoke(browser.uiComponent)
            return true
        }

        if (keyCode == KeyEvent.VK_F12) {
            val webTab = viewModel.currentTab() as? WebTab ?: return false
            pool.switchDevTools(webTab.id())
            return true
        }

        return false
    }

}