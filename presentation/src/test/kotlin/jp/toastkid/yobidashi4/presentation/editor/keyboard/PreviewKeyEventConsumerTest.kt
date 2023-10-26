package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PreviewKeyEventConsumerTest {

    @InjectMockKs
    private lateinit var previewKeyEventConsumer: PreviewKeyEventConsumer

    private lateinit var awtKeyEvent: java.awt.event.KeyEvent

    @MockK
    private lateinit var scrollBy: (Float) -> Unit

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { scrollBy.invoke(any()) } just Runs
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_A,
            'A'
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onKeyUp() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_RELEASED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_A,
            'A'
        )

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue(),
            mockk(),
            {},
            scrollBy
        )

        assertFalse(consumed)
    }

    @Test
    fun scrollUp() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_UP,
            'A'
        )

        val consumed = previewKeyEventConsumer.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue(),
            mockk(),
            {},
            scrollBy
        )

        assertTrue(consumed)
        verify { scrollBy(-16.dp.value) }
    }

}