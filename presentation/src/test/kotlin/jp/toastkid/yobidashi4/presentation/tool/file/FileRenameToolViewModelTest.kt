package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class FileRenameToolViewModelTest {

    private lateinit var subject: FileRenameToolViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }

        every { mainViewModel.showSnackbar(any(), any(), any()) } just Runs

        mockkStatic(Files::class)
        every { Files.copy(any<Path>(), any<Path>()) } returns mockk()

        subject = FileRenameToolViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun listState() {
        assertEquals(0, subject.listState().firstVisibleItemIndex)
    }

    @Test
    fun onValueChange() {
        assertEquals("img_", subject.input().text)

        subject.onValueChange(TextFieldValue("ABC"))

        assertEquals("ABC", subject.input().text)

        subject.clearInput()

        assertTrue(subject.input().text.isEmpty())
    }

    @Test
    fun rename() {
        subject.onValueChange(TextFieldValue("ABC"))
        val value = mockk<Path>()
        every { value.resolveSibling(any<String>()) } returns mockk()
        every { value.extension } returns "png"
        every { value.parent } returns value
        val slot = slot<() -> Unit>()
        every { mainViewModel.showSnackbar(any(), any(), capture(slot)) } just Runs
        every { mainViewModel.openFile(any()) } just Runs
        val capturingSlot = slot<(Path) -> Unit>()
        every { mainViewModel.registerDroppedPathReceiver(capture(capturingSlot)) } just Runs

        runBlocking {
            subject.collectDroppedPaths()
            capturingSlot.captured.invoke(value)

            subject.rename()

            verify(exactly = 1) { Files.copy(any<Path>(), any<Path>()) }
            verify(exactly = 1) { mainViewModel.showSnackbar(any(), any(), any()) }
            assertTrue(slot.isCaptured)

            slot.captured.invoke()
            verify { mainViewModel.openFile(any()) }

            subject.clearPaths()

            assertTrue(subject.items().isEmpty())
        }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEvent() {
        subject.onValueChange(TextFieldValue("ABC"))

        val consumed = subject.onKeyEvent(KeyEvent(Key.Enter, KeyEventType.KeyDown))

        assertTrue(consumed)
        verify(inverse = true) { Files.copy(any<Path>(), any<Path>()) }
        verify(inverse = true) { mainViewModel.showSnackbar(any(), any(), any()) }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventNotConsumedWithKeyReleasing() {
        subject.onValueChange(TextFieldValue("ABC"))

        val consumed = subject.onKeyEvent(KeyEvent(Key.Enter, KeyEventType.KeyUp))

        assertFalse(consumed)
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventNotConsumedWithOtherKey() {
        val consumed = subject.onKeyEvent(KeyEvent(Key.Zero, KeyEventType.KeyDown))

        assertFalse(consumed)
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventNotConsumedWithExistingComposition() {
        subject.onValueChange(TextFieldValue("ABC", composition = TextRange.Companion.Zero))

        val consumed = subject.onKeyEvent(KeyEvent(Key.Enter, KeyEventType.KeyDown))

        assertFalse(consumed)
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventNotConsumedWithTextIsEmpty() {
        subject.onValueChange(TextFieldValue())

        val consumed = subject.onKeyEvent(KeyEvent(Key.Enter, KeyEventType.KeyDown))

        assertFalse(consumed)
    }

    @Test
    fun dispose() {
        every { mainViewModel.unregisterDroppedPathReceiver() } just Runs

        subject.dispose()

        verify { mainViewModel.unregisterDroppedPathReceiver() }
    }

}