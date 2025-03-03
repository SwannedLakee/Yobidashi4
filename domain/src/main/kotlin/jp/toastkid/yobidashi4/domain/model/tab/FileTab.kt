package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path

data class FileTab(
    private val title: String,
    val items: List<Path>,
    val type: Type = Type.FIND
): Tab {

    override fun title(): String = title

    enum class Type {
        MUSIC, FIND;
    }
}