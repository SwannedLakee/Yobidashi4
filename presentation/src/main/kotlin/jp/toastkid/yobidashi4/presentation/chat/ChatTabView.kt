package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_clipboard
import jp.toastkid.yobidashi4.presentation.component.MultiLineTextField
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChatTabView(chatTab: ChatTab) {
    val viewModel = remember { ChatTabViewModel() }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.onKeyEvent { viewModel.onChatListKeyEvent(coroutineScope, it) }) {
            Box(Modifier.padding(8.dp).weight(1f)) {
                SelectionContainer {
                    MessageList(
                        viewModel.scrollState(),
                        viewModel.messages(),
                        viewModel::name,
                        viewModel::nameColor,
                        viewModel::clipText
                    )
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
                    .semantics { contentDescription = "Input message box." }
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

@Composable
private fun MessageList(
    listState: LazyListState,
    chatMessages: List<ChatMessage>,
    name: (String) -> String,
    nameColor: (String) -> Color,
    clipText: (String) -> Unit
) {
    LazyColumn(state = listState) {
        items(chatMessages) {
            Row {
                Spacer(modifier = Modifier.weight(0.2f))
                MessageContent(
                    it.text,
                    modifier = Modifier.padding(4.dp).padding(horizontal = 4.dp).weight(0.8f)
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    name(it.role),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = nameColor(it.role),
                    modifier = Modifier.padding(horizontal = 4.dp).align(Alignment.BottomStart)
                )

                Icon(
                    painterResource(Res.drawable.ic_clipboard),
                    contentDescription = "Clip this message.",
                    modifier = Modifier.clickable {
                        clipText(it.text)
                    }.align(Alignment.BottomEnd)
                )
            }

            Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
        }
    }
}