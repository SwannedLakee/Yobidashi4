package jp.toastkid.yobidashi4.domain.model.chat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChatTest {

    private lateinit var chat: Chat

    @BeforeEach
    fun setUp() {
        chat = Chat()
    }

    @Test
    fun list() {
        chat.addUserText("test")
        chat.addModelText("test")

        val list = chat.list()
        assertEquals(2, list.size)
        assertEquals("user", list[0].role)
        assertEquals("model", list[1].role)
    }

    @Test
    fun addModelTextsWithEmptyList() {
        chat.addUserText("test")
    }

    @Test
    fun removeLastUserText() {
        chat.removeLastUserText()

        chat.addUserText("test")
        chat.removeLastUserText()

        assertTrue(chat.list().isEmpty())
    }

}