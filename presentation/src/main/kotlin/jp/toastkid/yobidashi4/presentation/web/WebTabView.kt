package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.toArgb
import java.awt.Color
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun WebTabView(tab: WebTab) {
    val background = Color(MaterialTheme.colors.surface.toArgb())

    val viewModel = remember { WebTabViewModel() }

    Column {
        SwingPanel(
            factory = {
                val webUiComponent = viewModel.component(tab)
                webUiComponent.background = background
                webUiComponent
            },
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .focusRequester(viewModel.focusRequester())
        )

        Spacer(modifier = Modifier.height(viewModel.spacerHeight()))
    }

    LaunchedEffect(tab.id()) {
        withContext(Dispatchers.Unconfined) {
            viewModel.start(tab.id())
        }
    }
}