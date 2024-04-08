package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.presentation.component.MultiLineTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatTabView(chatTab: ChatTab) {
    val viewModel = remember { ChatTabViewModel() }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column {
            Box(Modifier.padding(8.dp).weight(1f)) {
                SelectionContainer {
                    LazyColumn(state = viewModel.scrollState()) {
                        items(viewModel.messages()) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    viewModel.name(it.role),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = viewModel.nameColor(it.role),
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                Text(it.text, fontSize = 16.sp)
                            }
                            Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
                        }
                    }

                    SideEffect {
                        coroutineScope.launch {
                            viewModel.scrollState().scrollToItem(viewModel.scrollState().layoutInfo.totalItemsCount)
                        }
                    }
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(viewModel.scrollState()), modifier = Modifier.fillMaxHeight().align(
                        Alignment.CenterEnd
                    )
                )
            }

            MultiLineTextField(
                viewModel.textInput(),
                viewModel.label(),
                Int.MAX_VALUE,
                viewModel::onValueChanged,
                modifier = Modifier.focusRequester(viewModel.focusRequester()).fillMaxWidth().weight(0.2f)
                    .onKeyEvent { viewModel.onKeyEvent(coroutineScope, it) }
            )
        }
    }

    DisposableEffect(chatTab) {
        viewModel.launch(chatTab.chat())

        onDispose {
            viewModel.update(chatTab)
        }
    }
}