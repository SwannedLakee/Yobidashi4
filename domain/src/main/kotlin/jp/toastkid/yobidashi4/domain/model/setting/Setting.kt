package jp.toastkid.yobidashi4.domain.model.setting

import java.nio.file.Path

interface Setting {

    fun darkMode(): Boolean

    fun setDarkMode(newValue: Boolean)

    fun articleFolder(): String

    fun articleFolderPath(): Path

    fun setArticleFolderPath(path: String)

    /*
    fun sorting(): String

    fun setSorting(newValue: Sorting)
*/

    fun userOffDay(): List<Pair<Int, Int>>

    fun setUseCaseSensitiveInFinder(use: Boolean)

    fun useCaseSensitiveInFinder(): Boolean

    fun setEditorBackgroundColor(color: java.awt.Color?)

    fun editorBackgroundColor(): java.awt.Color?

    fun setEditorForegroundColor(color: java.awt.Color?)

    fun editorForegroundColor(): java.awt.Color?

    fun resetEditorColorSetting()

    fun setEditorFontFamily(fontFamily: String?)

    fun editorFontFamily(): String?

    fun setEditorFontSize(size: Int?)

    fun editorFontSize(): Int

    fun editorConversionLimit(): Int

    fun mediaPlayerPath(): String?

    fun mediaFolderPath(): String?

    fun wrapLine(): Boolean

    fun switchWrapLine()

    fun getMaskingCount(): Int

    fun setMaskingCount(it: Int)

    fun userAgentName(): String

    fun setUserAgentName(newValue: String)

    fun useBackground(): Boolean

    fun switchUseBackground()

    fun chatApiKey(): String?

    fun save()

}