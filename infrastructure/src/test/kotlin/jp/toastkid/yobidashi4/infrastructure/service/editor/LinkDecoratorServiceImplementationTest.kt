package jp.toastkid.yobidashi4.infrastructure.service.editor

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.IOException
import java.net.URL

internal class LinkDecoratorServiceImplementationTest {

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test() {
        assertEquals("www.yahoo.co.jp", LinkDecoratorServiceImplementation().invoke("www.yahoo.co.jp"))
        mockkStatic(Jsoup::class)
        val document = mockk<Document>()
        every { Jsoup.parse(any<URL>(), any()) }.returns(document)
        every { document.title() } returns "Yahoo! JAPAN"
        assertEquals(
            "[Yahoo! JAPAN](https://www.yahoo.co.jp)",
            LinkDecoratorServiceImplementation().invoke("https://www.yahoo.co.jp")
        )
    }

    @Test
    fun testException() {
        mockkStatic(Jsoup::class)
        every { Jsoup.parse(any<URL>(), any()) }.throws(IOException())

        assertEquals("https://www.yahoo.co.jp", LinkDecoratorServiceImplementation().invoke("https://www.yahoo.co.jp"))
    }

}
