package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextLineView(text: String, textStyle: TextStyle, modifier: Modifier) {
    val viewModel = remember { TextLineViewModel() }

    Text(
        viewModel.annotatedString(),
        style = textStyle,
        modifier = modifier.onPointerEvent(PointerEventType.Release) {
            //viewModel.onPointerReleased(it)
        }.pointerInput(Unit) {
            awaitEachGesture {
                viewModel.onPointerReleased(awaitPointerEvent())
            }
        },
        onTextLayout = viewModel::putLayoutResult
    )

    LaunchedEffect(text) {
        withContext(Dispatchers.IO) {
            viewModel.launch(text)
        }
    }
}
