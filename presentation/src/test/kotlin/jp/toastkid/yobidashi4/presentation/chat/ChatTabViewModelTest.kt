package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.service.chat.ChatService
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class ChatTabViewModelTest {

    private lateinit var subject: ChatTabViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var service: ChatService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { service } bind(ChatService::class)
                }
            )
        }

        every { service.send(any(), any()) } returns ""
        every { service.setChat(any()) } just Runs
        every { service.messages() } returns emptyList()
        every { mainViewModel.showSnackbar(any()) } just Runs

        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        subject = ChatTabViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun messages() {
        val messages = subject.messages()

        assertTrue(messages.isEmpty())
        verify { service.messages() }
    }

    @Test
    fun noopSend() {
        runBlocking {
            subject.send(CoroutineScope(Dispatchers.Unconfined))

            verify(inverse = true) { service.send(any(), any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun send() {
        runDesktopComposeUiTest {
            setContent {
                rememberCoroutineScope().launch {
                    subject.onValueChanged(TextFieldValue("test"))

                    subject.send(CoroutineScope(Dispatchers.Unconfined))

                    verify { service.send(any(), any()) }
                }
            }
        }
    }

    @Test
    fun textInput() {
        assertTrue(subject.textInput().text.isEmpty())
    }

    @Test
    fun focusRequester() {
        assertNotNull(subject.focusRequester())
    }

    @Test
    fun launch() {
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs

        subject.launch(mockk())

        verify { subject.focusRequester() }
        verify { service.setChat(any()) }
    }

    @Test
    fun update() {
        val chatTab = mockk<ChatTab>()
        every { mainViewModel.replaceTab(any(),  any()) } just Runs
        every { service.getChat() } returns mockk()

        subject.update(chatTab)

        verify { mainViewModel.replaceTab(any(),  any()) }
        verify { service.getChat() }
    }

    @Test
    fun label() {
        assertTrue(subject.label().isNotBlank())
    }

    @Test
    fun name() {
        assertEquals("You", subject.name("user"))
        assertEquals("Assistant", subject.name("model"))
        assertEquals("Unknown", subject.name("unknown"))
    }

    @Test
    fun scrollState() {
        assertNotNull(subject.scrollState())
    }

    @Test
    fun nameColor() {
        assertNotEquals(subject.nameColor("model"), subject.nameColor("model2"))
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                subject = spyk(subject)
                coEvery { subject.send(any()) } just Runs
                subject.onValueChanged(TextFieldValue("test"))

                val coroutineScope = rememberCoroutineScope()
                val consumed = subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.Enter, KeyEventType.KeyUp, isCtrlPressed = true)
                )
                assertTrue(consumed)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventWithComposition() {
        runDesktopComposeUiTest {
            setContent {
                subject.onValueChanged(TextFieldValue("test", composition = TextRange.Zero))

                val coroutineScope = rememberCoroutineScope()
                val consumed = subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.Enter, KeyEventType.KeyUp, isCtrlPressed = true)
                )
                assertFalse(consumed)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventWithoutCtrlMask() {
        runDesktopComposeUiTest {
            setContent {
                subject.onValueChanged(TextFieldValue("test"))

                val coroutineScope = rememberCoroutineScope()
                val consumed = subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.Enter, KeyEventType.KeyUp, isAltPressed = true)
                )
                assertFalse(consumed)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEventOtherKey() {
        runDesktopComposeUiTest {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                val consumed = subject.onKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.One, KeyEventType.KeyUp, isCtrlPressed = true)
                )
                assertFalse(consumed)
            }
        }
    }

    @Test
    fun clipText() {
        subject.clipText("test")

        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
        verify { mainViewModel.showSnackbar(any()) }
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onChatListKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                subject = spyk(subject)

                val coroutineScope = rememberCoroutineScope()
                val consumed = subject.onChatListKeyEvent(
                    coroutineScope,
                    KeyEvent(Key.DirectionUp, KeyEventType.KeyDown)
                )
                assertTrue(consumed)
            }
        }
    }

}