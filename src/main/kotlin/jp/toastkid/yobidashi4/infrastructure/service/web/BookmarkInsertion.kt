package jp.toastkid.yobidashi4.infrastructure.service.web

import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.cef.callback.CefContextMenuParams
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BookmarkInsertion {

    operator fun invoke(params: CefContextMenuParams? = null, latestUrl: String?) {
        val mainViewModel = object : KoinComponent { val vm: MainViewModel by inject() }.vm

        val item = when {
            params?.linkUrl != null && params.linkUrl.isNotBlank() ->
                makeBookmarkItemWithUrl(params.linkUrl)
            params?.sourceUrl != null && params.sourceUrl.isNotBlank() ->
                makeBookmarkItemWithUrl(params.sourceUrl)
            params?.pageUrl != null && params.pageUrl.isNotBlank() ->
                Bookmark(mainViewModel.currentTab()?.title() ?: "", url = params.pageUrl)
            else ->
                Bookmark(mainViewModel.currentTab()?.title() ?: "", url = latestUrl ?: "")
        }
        object : KoinComponent { val repository: BookmarkRepository by inject() }.repository.add(item)
        mainViewModel.showSnackbar("Add bookmark: $item")
    }

    private fun makeBookmarkItemWithUrl(url: String): Bookmark {
        return Bookmark(url, url = url)
    }

}