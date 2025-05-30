package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MouseButton
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MessageContentKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(MessageContentViewModel::class)
        every { anyConstructed<MessageContentViewModel>().storeImage(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun messageContent() {
        runComposeUiTest {
            setContent {
                MessageContent(
                    "test\n* **test**\n* ***Good***",
                    null,
                    Modifier
                )
                MessageContent(
                    "image",
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBASfqKgwAAAAASUVORK5CYII=",
                    Modifier
                )
            }

            onNodeWithContentDescription("image", useUnmergedTree = true)
                .performMouseInput {
                    enter()
                    press(MouseButton.Secondary)
                }
        }
    }

}