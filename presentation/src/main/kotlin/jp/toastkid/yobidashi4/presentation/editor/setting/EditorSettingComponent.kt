package jp.toastkid.yobidashi4.presentation.editor.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.awt.GraphicsEnvironment

@Composable
internal fun EditorSettingComponent(modifier: Modifier) {
    val viewModel = remember { EditorSettingViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = modifier.padding(8.dp)
    ) {
        Column {
            EditorSettingDropdown(
                GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames.toList(),
                "Font family: ${viewModel.editorFontFamily()}",
                viewModel.isOpenFontFamily(),
                viewModel::closeFontFamily,
                {
                    viewModel.setEditorFontFamily(it.toString())
                },
                Modifier.clickable(onClick = viewModel::openFontFamily)
            )

            EditorSettingDropdown(
                (12 .. 24).toList(),
                "Font Size: ${viewModel.editorFontSize()}",
                viewModel.isOpenFontSize(),
                viewModel::closeFontSize,
                viewModel::setEditorFontSize,
                Modifier.clickable(onClick = viewModel::openFontSize)
            )

            Text("Reset color setting", modifier = Modifier.clickable {
                viewModel.reset()
            })
        }
    }
}

@Composable
private fun <T> EditorSettingDropdown(
    items: Collection<T>,
    displayText: String,
    open: Boolean,
    onClose: () -> Unit,
    onSelectValue: (T) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {
        Text(displayText)
        DropdownMenu(
            open,
            onDismissRequest = onClose
        ) {
            items.forEach {
                DropdownMenuItem(
                    onClick = {
                        onClose()
                        onSelectValue(it)
                    }
                ) {
                    Text("$it")
                }
            }
        }
    }
}