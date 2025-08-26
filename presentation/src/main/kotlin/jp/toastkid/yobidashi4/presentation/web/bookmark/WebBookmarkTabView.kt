package jp.toastkid.yobidashi4.presentation.web.bookmark

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightDropdownMenuItem
import jp.toastkid.yobidashi4.presentation.component.LoadIcon

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun WebBookmarkTabView(tab: WebBookmarkTab) {
    val viewModel = remember { WebBookmarkTabViewModel() }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = Modifier.onKeyEvent {
            return@onKeyEvent viewModel.scrollAction(coroutineScope, it.key, it.isCtrlPressed)
        }.focusRequester(viewModel.focusRequester()).focusable(true)
    ) {
        Box {
            LazyColumn(
                state = viewModel.listState(),
                userScrollEnabled = true,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                itemsIndexed(
                    viewModel.bookmarks(),
                    { index, item -> item.title + item.url + index }
                ) { _, bookmark ->
                    val cursorOn = remember { mutableStateOf(false) }
                    val backgroundColor = animateColorAsState(if (cursorOn.value) MaterialTheme.colors.primary else Color.Transparent)

                    WebBookmarkItemRow(
                        bookmark,
                        {
                            viewModel.openUrl(bookmark.url, it)
                            viewModel.closeDropdown()
                        },
                        {
                            viewModel.browseUri(bookmark.url)
                            viewModel.closeDropdown()
                        },
                        viewModel::clipText,
                        {
                            viewModel.delete(bookmark)
                        },
                        viewModel.openingDropdown(bookmark),
                        viewModel::closeDropdown,
                        if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                        Modifier.animateItem()
                            .combinedClickable(
                                enabled = true,
                                onClick = {
                                    viewModel.openUrl(bookmark.url, false)
                                },
                                onLongClick = {
                                    viewModel.openUrl(bookmark.url, true)
                                }
                            )
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    viewModel.onPointerEvent(awaitPointerEvent(), bookmark)
                                }
                            }
                            .drawBehind { drawRect(backgroundColor.value) }
                            .onPointerEvent(PointerEventType.Enter) {
                                cursorOn.value = true
                            }
                            .onPointerEvent(PointerEventType.Exit) {
                                cursorOn.value = false
                            }
                    )
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.listState()),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )

            DisposableEffect(tab) {
                viewModel.launch(coroutineScope, tab.scrollPosition())

                onDispose {
                    viewModel.update(tab)
                }
            }
        }
    }
}

@Composable
private fun WebBookmarkItemRow(
    bookmark: Bookmark,
    openUrl: (Boolean) -> Unit,
    browseUri: () -> Unit,
    clipText: (String) -> Unit,
    onDelete: () -> Unit,
    openingDropdown: Boolean,
    closeDropdown: () -> Unit,
    textColor: Color,
    modifier: Modifier
) {
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            LoadIcon(bookmark.url, Modifier.size(32.dp).padding(start = 4.dp).padding(horizontal = 4.dp))
            Column(modifier = Modifier
                .padding(horizontal = 16.dp)
            ) {
                Text(bookmark.title, color = textColor)
                Text(bookmark.url, maxLines = 1, overflow = TextOverflow.Ellipsis, color = textColor)
                Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
            }
        }

        DropdownMenu(
            expanded = openingDropdown,
            onDismissRequest = closeDropdown
        ) {
            HoverHighlightDropdownMenuItem("Open") {
                openUrl(false)
            }

            HoverHighlightDropdownMenuItem("Open background") {
                openUrl(true)
            }

            DropdownMenuItem(
                onClick = browseUri
            ) {
                Text(
                    "Open with browser",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }
            DropdownMenuItem(
                onClick = {
                    clipText(bookmark.title)
                }
            ) {
                Text(
                    "Copy title",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }
            DropdownMenuItem(
                onClick = {
                    clipText(bookmark.url)
                }
            ) {
                Text(
                    "Copy URL",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }
            DropdownMenuItem(
                onClick = {
                    clipText("[${bookmark.title}](${bookmark.url})")
                }
            ) {
                Text(
                    "Clip markdown link",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }
            HoverHighlightDropdownMenuItem("Delete") {
                onDelete()
            }
        }
    }
}