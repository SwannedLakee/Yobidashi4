package jp.toastkid.yobidashi4.infrastructure.service.web

import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.infrastructure.model.web.ContextMenu
import jp.toastkid.yobidashi4.infrastructure.service.web.menu.QuickStoreActionBehavior
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.cef.browser.CefBrowser
import org.cef.callback.CefContextMenuParams
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URI
import javax.imageio.ImageIO

class CefContextMenuAction : KoinComponent {

    private val viewModel: MainViewModel by inject()

    operator fun invoke(
        browser: CefBrowser?,
        params: CefContextMenuParams?,
        selectedText: String,
        commandId: Int
        ) {
        when (commandId) {
            ContextMenu.RELOAD.id -> {
                browser?.reload()
            }

            ContextMenu.OPEN_OTHER_TAB.id -> {
                params?.linkUrl?.let {
                    viewModel.openUrl(it, false)
                }
            }

            ContextMenu.OPEN_BACKGROUND.id -> {
                params?.linkUrl?.let {
                    viewModel.openUrl(it, true)
                }
            }

            ContextMenu.CLIP_LINK.id -> {
                params?.linkUrl?.let {
                    ClipboardPutterService().invoke(it)
                }
            }

            ContextMenu.SEARCH_WITH_SELECTED_TEXT.id -> {
                viewModel.webSearch(selectedText)
            }

            ContextMenu.RESET_ZOOM.id -> {
                browser?.let {
                    it.zoomLevel = 0.0
                }
            }

            ContextMenu.DOWNLOAD.id -> {
                val sourceUrl = params?.sourceUrl ?: return
                browser?.startDownload(sourceUrl)
            }

            ContextMenu.QUICK_STORE_IMAGE.id -> {
                val sourceUrl = params?.sourceUrl ?: return
                QuickStoreActionBehavior().invoke(URI(sourceUrl).toURL())
            }

            ContextMenu.ADD_BOOKMARK.id -> {
                BookmarkInsertion()(params, browser?.url)
            }

            ContextMenu.CLIP_IMAGE.id -> {
                val sourceUrl = params?.sourceUrl ?: return
                val image = ImageIO.read(URI(sourceUrl).toURL()) ?: return
                ClipboardPutterService().invoke(image)
            }

            ContextMenu.CLIP_PAGE_LINK.id -> {
                params ?: return
                val link = params.linkUrl ?: params.sourceUrl ?: params.pageUrl ?: return
                ClipboardPutterService().invoke(link)
            }

            ContextMenu.CLIP_AS_MARKDOWN_LINK.id -> {
                ClipboardPutterService().invoke("[${viewModel.currentTab()?.title()}](${params?.pageUrl})")
            }

            ContextMenu.SAVE_AS_PDF.id -> {
                browser?.printToPDF("${browser.identifier}.pdf", null, null)
            }

            ContextMenu.OPEN_WITH_OTHER_BROWSER.id -> {
                viewModel.browseUri(params?.linkUrl ?: params?.sourceUrl ?: selectedText)
            }

            ContextMenu.SEARCH_WITH_IMAGE.id -> {
                params?.sourceUrl?.let {
                    viewModel.openUrl(SearchSite.SEARCH_WITH_IMAGE.make(it).toString(), false)
                }
            }

            ContextMenu.CLIP_TEXT.id -> {
                params?.selectionText?.let {
                    ClipboardPutterService().invoke(it)
                }
            }

            ContextMenu.DEVELOPER_TOOL.id -> {
                val webTab = viewModel.currentTab() as? WebTab ?: return
                object : KoinComponent { val pool: WebViewPool by inject() }.pool.switchDevTools(webTab.id())
            }

            else -> Unit
        }
    }

}