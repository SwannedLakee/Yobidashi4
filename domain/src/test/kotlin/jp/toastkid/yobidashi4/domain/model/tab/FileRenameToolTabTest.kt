package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FileRenameToolTabTest {

    @Test
    fun test() {
        val tab = FileRenameToolTab()

        assertNotNull(tab.title())
        assertTrue(tab.iconPath()?.startsWith("images/icon/") ?: false)
    }

}