package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.chat.Source
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightRow

@Composable
internal fun MessageContent(
    text: String,
    base64Image: String? = null,
    sources: List<Source>,
    modifier: Modifier
) {
    val viewModel = remember { MessageContentViewModel() }

    Column(modifier) {
        text.split("\n").forEach {
            val listLine = it.startsWith("* ")
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (listLine) {
                    Text(
                        "・ ",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Text(
                    viewModel.lineText(listLine, it),
                    fontSize = 16.sp
                )
            }
        }

        if (base64Image != null) {
            DisableSelection {
                ContextMenuArea(
                    {
                        listOf(
                            ContextMenuItem("Store image") {
                                viewModel.storeImage(base64Image)
                            }
                        )
                    },
                    state = viewModel.contextMenuState()
                ) {
                    Image(
                        viewModel.image(base64Image),
                        contentDescription = text,
                    )
                }
            }
        }

        if (sources.isNotEmpty()) {
            Row {
                sources.forEachIndexed { index, source ->
                    HoverHighlightRow(
                        modifier = Modifier
                            .clickable { viewModel.openLink(source.url) }
                            .semantics { contentDescription = "$index,${source}" }
                    ) {
                        Text(source.title)
                    }
                }
            }
        }
    }
}