package jp.toastkid.yobidashi4.presentation.web.bookmark

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerEvent
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.lib.mouse.PointerEventAdapter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path

class WebBookmarkTabViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val repository: BookmarkRepository by inject()

    private val bookmarks = mutableStateListOf<Bookmark>()

    private val state = LazyListState()

    private val focusRequester = FocusRequester()

    private val scrollAction = KeyboardScrollAction(state)

    private val faviconFolder = WebIcon()

    fun bookmarks(): List<Bookmark> = bookmarks

    fun listState() = state

    fun focusRequester() = focusRequester

    fun scrollAction(coroutineScope: CoroutineScope, key: Key, controlDown: Boolean) =
        scrollAction.invoke(coroutineScope, key, controlDown)

    fun launch(coroutineScope: CoroutineScope, scrollPosition: Int) {
        repository.list().forEach(bookmarks::add)
        focusRequester().requestFocus()
        coroutineScope.launch {
            state.scrollToItem(scrollPosition)
        }

        faviconFolder.makeFolderIfNeed()
    }

    fun delete(bookmark: Bookmark) {
        repository.delete(bookmark)
        bookmarks.remove(bookmark)
        closeDropdown()
    }

    fun openUrl(url: String, onBackground: Boolean) {
        viewModel.openUrl(url, onBackground)
    }

    fun browseUri(url: String) {
        viewModel.browseUri(url)
    }

    private val currentDropdownItem = mutableStateOf<Bookmark?>(null)

    fun openingDropdown(item: Bookmark) = currentDropdownItem.value == item

    fun openDropdown(item: Bookmark) {
        currentDropdownItem.value = item
    }

    fun closeDropdown() {
        currentDropdownItem.value = null
    }

    private val pointerEventAdapter = PointerEventAdapter()

    @OptIn(ExperimentalComposeUiApi::class)
    fun onPointerEvent(pointerEvent: PointerEvent, bookmark: Bookmark) {
        if (pointerEventAdapter.isSecondaryClick(pointerEvent)
            && openingDropdown(bookmark).not()
        ) {
            openDropdown(bookmark)
        }
    }

    fun update(tab: WebBookmarkTab) {
        viewModel.updateScrollableTab(tab, state.firstVisibleItemIndex)
    }

    fun findFaviconPath(url: String): Path? {
        return faviconFolder.find(url)
    }

    fun clipText(text: String) {
        ClipboardPutterService().invoke(text)
        closeDropdown()
    }

}