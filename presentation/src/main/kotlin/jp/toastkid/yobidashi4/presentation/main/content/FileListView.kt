package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItem
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun FileListView(paths: List<Path>, modifier: Modifier = Modifier) {
    val viewModel = remember { FileListViewModel() }

    LaunchedEffect(paths.size) {
        viewModel.start(paths)
    }

    val coroutineScope = rememberCoroutineScope()

    val oddBackground = MaterialTheme.colors.primary.copy(alpha = 0.5f)
    val evenBackground = MaterialTheme.colors.surface.copy(alpha = 0.5f)

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = modifier
    ) {
        Box(modifier = Modifier) {
            LazyColumn(
                state = viewModel.listState(),
                userScrollEnabled = true,
                modifier = Modifier
                    .onKeyEvent { keyEvent ->
                        viewModel.onKeyEvent(coroutineScope, keyEvent)
                    }
                    .semantics { contentDescription = "File list" }
            ) {
                stickyHeader {
                    SingleLineTextField(
                        viewModel.keyword(),
                        "Keyword",
                        viewModel::onValueChange,
                        viewModel::clearInput
                    )
                }

                itemsIndexed(viewModel.items(), key = { i, fileListItem -> "${i}_" + fileListItem.path}) { index, fileListItem ->
                    val underlay = if (fileListItem.selected) oddBackground
                    else if (index % 2 == 0) evenBackground
                    else Color.Transparent
                    val cursorOn = viewModel.focusingItem(fileListItem)
                    val backgroundColor = animateColorAsState(
                        if (cursorOn) MaterialTheme.colors.primary else Color.Transparent
                    )

                    FileListItemRow(
                        fileListItem,
                        backgroundColor.value,
                        viewModel.openingDropdown(fileListItem),
                        cursorOn,
                        viewModel::closeDropdown,
                        { viewModel.items().filter { it.selected }.map { it.path } },
                        viewModel::openFile,
                        { viewModel.edit(fileListItem.path) },
                        { viewModel.preview(fileListItem.path) },
                        { viewModel.slideshow(fileListItem.path) },
                        viewModel::clipText,
                        modifier.animateItem()
                            .combinedClickable(
                                enabled = true,
                                onClick = {
                                    viewModel.onSingleClick(fileListItem)
                                },
                                onLongClick = {
                                    viewModel.onLongClick(fileListItem)
                                },
                                onDoubleClick = {
                                    viewModel.onDoubleClick(fileListItem)
                                }
                            )
                            .drawBehind { drawRect(underlay) }
                            .onPointerEvent(PointerEventType.Enter) {
                                viewModel.focusItem(fileListItem)
                            }
                            .onPointerEvent(PointerEventType.Exit) {
                                viewModel.unFocusItem()
                            }
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    viewModel.onPointerEvent(awaitPointerEvent(), index)
                                }
                            }
                            .onKeyEvent {
                                viewModel.onKeyEventFromCell(it, fileListItem)
                            }
                            .semantics { contentDescription = fileListItem.path.nameWithoutExtension }
                    )
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.listState()),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )
            HorizontalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.horizontalScrollState()),
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun FileListItemRow(
    fileListItem: FileListItem,
    backgroundColor: Color,
    openOption: Boolean,
    cursorOn: Boolean,
    closeOption: () -> Unit,
    selectedFiles: () -> List<Path>,
    openFile: (Path) -> Unit,
    edit: () -> Unit,
    preview: () -> Unit,
    slideshow: () -> Unit,
    clipText: (String) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column(modifier = Modifier
            .drawBehind { drawRect(backgroundColor) }
            .padding(horizontal = 16.dp)
        ) {
            val textColor = if (cursorOn) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
            Text(
                fileListItem.path.nameWithoutExtension,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor
            )
            fileListItem.subText()?.let {
                Text(
                    it,
                    color = textColor
                )
            }
            Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
        }

        DropdownMenu(
            openOption,
            onDismissRequest = closeOption
        ) {
            DropdownMenuItem(
                onClick = {
                    selectedFiles().ifEmpty { listOf(fileListItem.path) }.forEach(openFile)
                    closeOption()
                }
            ) {
                Text(
                    "Open",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }

            if (fileListItem.editable) {
                DropdownMenuItem(
                    onClick = {
                        edit()
                        closeOption()
                    }
                ) {
                    Text(
                        "Edit",
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        preview()
                        closeOption()
                    }
                ) {
                    Text(
                        "Preview",
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        openFile(fileListItem.path)
                        closeOption()
                    }
                ) {
                    Text(
                        "Open background",
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        slideshow()
                        closeOption()
                    }
                ) {
                    Text(
                        "Slideshow",
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        clipText(fileListItem.path.nameWithoutExtension)
                    }
                ) {
                    Text(
                        "Copy title",
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        clipText("[[${fileListItem.path.nameWithoutExtension}]]")
                    }
                ) {
                    Text(
                        "Clip internal link",
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }
            }
        }
    }
}