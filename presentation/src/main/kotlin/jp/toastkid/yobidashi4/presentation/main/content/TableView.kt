package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.Icon
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_edit
import jp.toastkid.yobidashi4.library.resources.ic_markdown
import jp.toastkid.yobidashi4.presentation.component.VerticalDivider
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun TableView(tab: TableTab) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { TableViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = Modifier.onKeyEvent {
            viewModel.scrollAction(coroutineScope, it.key, it.isCtrlPressed)
        }.focusRequester(viewModel.focusRequester()).focusable(true)
    ) {
        Box {
            val horizontalScrollState = rememberScrollState()
            LazyColumn(
                state = viewModel.listState(),
                userScrollEnabled = true
            ) {
                stickyHeader {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        tab.items().header().forEachIndexed { index, item ->
                            if (index != 0) {
                                VerticalDivider(modifier = Modifier.height(32.dp).padding(vertical = 1.dp))
                            }
                            val headerCursorOn = remember { mutableStateOf(false) }
                            val headerColumnBackgroundColor = animateColorAsState(
                                if (headerCursorOn.value) MaterialTheme.colors.primary
                                else if (viewModel.listState().firstVisibleItemIndex != 0) MaterialTheme.colors.surface
                                else Color.Transparent
                            )

                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier
                                    .fillParentMaxHeight(0.05f)
                                    .clickable {
                                        viewModel.sort(index, tab.items())
                                    }
                                    .onPointerEvent(PointerEventType.Enter) {
                                        headerCursorOn.value = true
                                    }
                                    .onPointerEvent(PointerEventType.Exit) {
                                        headerCursorOn.value = false
                                    }
                                    .drawBehind { drawRect(headerColumnBackgroundColor.value) }
                                    .weight(viewModel.makeWeight(index))
                            ) {
                                Text(
                                    item.toString(),
                                    color = if (headerCursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                    Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
                }

                items(viewModel.items(), Any::hashCode) {
                    TableRow(
                        it,
                        viewModel::openMarkdownPreview,
                        viewModel::edit,
                        { viewModel.highlight(it) },
                        viewModel::makeText
                    )
                }
            }
            VerticalScrollbar(adapter = rememberScrollbarAdapter(viewModel.listState()), modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd))
            HorizontalScrollbar(adapter = rememberScrollbarAdapter(horizontalScrollState), modifier = Modifier.fillMaxWidth().align(
                Alignment.BottomCenter))

            DisposableEffect(tab) {
                coroutineScope.launch {
                    viewModel.start(tab)
                }

                onDispose {
                    viewModel.onDispose(tab)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun LazyItemScope.TableRow(
    article: Array<Any>,
    openMarkdownPreview: (String) -> Unit,
    edit: (String) -> Unit,
    highlight: (String) -> AnnotatedString,
    makeText: (Any) -> String
) {
    SelectionContainer {
        Column(modifier = Modifier.animateItem()) {
            val cursorOn = remember { mutableStateOf(false) }
            val rowBackgroundColor = animateColorAsState(
                if (cursorOn.value) MaterialTheme.colors.primary else Color.Transparent
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .drawBehind { drawRect(rowBackgroundColor.value) }
                    .onPointerEvent(PointerEventType.Enter) {
                        cursorOn.value = true
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        cursorOn.value = false
                    }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_markdown),
                    contentDescription = "Open preview",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.padding(vertical = 4.dp)
                        .padding(start = 8.dp)
                        .clickable {
                            openMarkdownPreview(article[0].toString())
                        }
                )

                Icon(
                    painter = painterResource(Res.drawable.ic_edit),
                    contentDescription = "Open file",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.padding(vertical = 4.dp)
                        .padding(start = 4.dp)
                        .clickable {
                            edit(article[0].toString())
                        }
                )

                article.forEachIndexed { index, any ->
                    if (index != 0) {
                        VerticalDivider(modifier = Modifier.heightIn(min = 36.dp).padding(vertical = 1.dp))
                    }
                    if (any is Collection<*>) {
                        Column(modifier = Modifier.weight(1f)) {
                            any.forEach { line ->
                                Text(
                                    highlight(line.toString()),
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                )
                                Divider(modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                        return@forEachIndexed
                    }
                    Text(
                        makeText(any),
                        color = if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .weight(if (index == 0) 0.4f else 1f)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
            Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
        }
    }
}
