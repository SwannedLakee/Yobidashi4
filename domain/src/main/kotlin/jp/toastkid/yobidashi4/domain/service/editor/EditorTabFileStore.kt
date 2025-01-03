package jp.toastkid.yobidashi4.domain.service.editor

import java.io.IOException
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorTabFileStore {

    operator fun invoke(tab: EditorTab, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        if (tab.closeable()) {
            return
        }

        CoroutineScope(dispatcher).launch {
            val text = tab.getContent()
            val textArray = text.toString().toByteArray()

            if (textArray.isEmpty()) {
                return@launch
            }

            try {
                Files.write(tab.path, textArray)
                tab.setContent(text, true)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}