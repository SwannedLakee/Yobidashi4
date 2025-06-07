package jp.toastkid.yobidashi4.infrastructure.model.setting

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

class SettingImplementationTest {

    private lateinit var subject: SettingImplementation

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Path::class)
        every { Path.of(any<String>()) } returns path
        every { path.parent } returns path
        every { path.exists() } returns true
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.newBufferedReader(any()) } returns BufferedReader(InputStreamReader(InputStream.nullInputStream()))
        every { Files.createDirectory(any()) } returns path
        every { Files.newBufferedWriter(any()) } returns BufferedWriter(StringWriter())

        subject = SettingImplementation()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun makeFolderIfNeed() {
        every { Files.exists(any()) } returns false
        every { Files.createDirectory(any()) } returns path
        every { Files.createFile(any()) } returns path

        subject = SettingImplementation()

        verify { Files.exists(any()) }
        verify { Files.createDirectory(any()) }
        verify { Files.createFile(any()) }
    }

    @Test
    fun darkMode() {
        assertFalse(subject.darkMode())

        subject.setDarkMode(true)

        assertTrue(subject.darkMode())
    }

    @Test
    fun articleFolder() {
        assertTrue(subject.articleFolder().isEmpty())
    }

    @Test
    fun articleFolderPath() {
        assertNotNull(subject.articleFolderPath())
        verify { Path.of(any<String>()) }
    }

    @Test
    fun userOffDayInitial() {
        assertTrue(subject.userOffDay().isEmpty())
    }

    @Test
    fun userOffDay() {
        every { Files.newBufferedReader(any()) } returns BufferedReader(InputStreamReader("""
user_off_day=12/29,12/30
        """.trimIndent().byteInputStream()))
        subject = SettingImplementation()

        val userOffDay = subject.userOffDay()
        assertEquals(2, userOffDay.size)
        assertEquals(12, userOffDay.get(0).first)
        assertEquals(29, userOffDay.get(0).second)
        assertEquals(12, userOffDay.get(1).first)
        assertEquals(30, userOffDay.get(1).second)
    }

    @Test
    fun setUseCaseSensitiveInFinder() {
        assertFalse(subject.useCaseSensitiveInFinder())

        subject.setUseCaseSensitiveInFinder(true)

        assertTrue(subject.useCaseSensitiveInFinder())
    }

    @Test
    fun setEditorBackgroundColor() {
        assertEquals(java.awt.Color(225, 225, 225, 255),  subject.editorBackgroundColor())

        subject.setEditorBackgroundColor(java.awt.Color.BLACK)
        subject.setEditorBackgroundColor(null)
    }

    @Test
    fun setEditorForegroundColor() {
        assertNull(subject.editorForegroundColor())

        subject.setEditorForegroundColor(java.awt.Color.WHITE)

        assertEquals(java.awt.Color.WHITE,  subject.editorForegroundColor())
    }

    @Test
    fun resetEditorColorSetting() {
        subject.setEditorForegroundColor(java.awt.Color.WHITE)

        subject.resetEditorColorSetting()

        assertEquals(java.awt.Color(225, 225, 225, 255),  subject.editorBackgroundColor())
        assertNull(subject.editorForegroundColor())
    }

    @Test
    fun editorFontFamily() {
        assertNull(subject.editorFontFamily())

        subject.setEditorFontFamily("test")

        assertEquals("test", subject.editorFontFamily())
    }

    @Test
    fun editorFontFamilyWithNull() {
        assertNull(subject.editorFontFamily())

        subject.setEditorFontFamily(null)

        assertNull(subject.editorFontFamily())
    }

    @Test
    fun editorFontSize() {
        assertEquals(14, subject.editorFontSize())

        subject.setEditorFontSize(1)

        assertEquals(1, subject.editorFontSize())

        subject.setEditorFontSize(null)

        assertEquals(1, subject.editorFontSize())
    }

    @Test
    fun editorConversionLimit() {
        assertTrue(subject.editorConversionLimit() > 0)
    }

    @Test
    fun mediaPlayerPath() {
        assertNull(subject.mediaPlayerPath())
    }

    @Test
    fun mediaFolderPath() {
        assertNull(subject.mediaFolderPath())
    }

    @Test
    fun save() {
        subject.save()

        verify(inverse = true) { Files.createDirectory(any()) }
    }

    @Test
    fun saveWithFolderCreation() {
        every { path.exists() } returns false

        subject.save()

        verify { Files.createDirectory(any()) }
    }

    @Test
    fun switchWrapLine() {
        assertFalse(subject.wrapLine())

        subject.switchWrapLine()

        assertTrue(subject.wrapLine())
    }

    @Test
    fun setMaskingCount() {
        assertEquals(20, subject.getMaskingCount())

        subject.setMaskingCount(3)

        assertEquals(3, subject.getMaskingCount())

        subject.update("number_place_masking_count", "invalid")

        assertEquals(20, subject.getMaskingCount())
    }

    @Test
    fun setUserAgentName() {
        assertTrue(subject.userAgentName().isEmpty())

        subject.setUserAgentName("test")

        assertEquals("test", subject.userAgentName())
    }

    @Test
    fun switchUseBackground() {
        assertTrue(subject.useBackground())

        subject.switchUseBackground()

        assertFalse(subject.useBackground())

        subject.update("use_background", "test")

        assertTrue(subject.useBackground())
    }

    @Test
    fun chatApiKey() {
        assertNull(subject.chatApiKey())

        subject.update("chat_api_key", "test")

        assertEquals("test", subject.chatApiKey())
    }

    @Test
    fun test() {
        subject.setArticleFolderPath("test")

        assertEquals("test", subject.articleFolder())
    }

    @Test
    fun items() {
        assertTrue(subject.items().isEmpty())
    }

    @Test
    fun update() {
        subject.update("test", "new value")
        assertEquals("new value", subject.items().get("test"))
    }

}