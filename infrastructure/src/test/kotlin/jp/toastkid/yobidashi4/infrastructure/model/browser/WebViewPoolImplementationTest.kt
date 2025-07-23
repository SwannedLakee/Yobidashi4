package jp.toastkid.yobidashi4.infrastructure.model.browser

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.infrastructure.service.web.CefClientFactory
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WebViewPoolImplementationTest {

    private lateinit var subject: WebViewPoolImplementation

    @MockK
    private lateinit var cefClient: CefClient

    @MockK
    private lateinit var cefBrowser: CefBrowser

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { cefClient.createBrowser(any(), any(), any()) }.returns(cefBrowser)
        every { cefClient.doClose(any()) } returns true
        every { cefClient.dispose() } just Runs
        every { cefBrowser.uiComponent }.returns(mockk())
        every { cefBrowser.close(any()) } just Runs
        every { cefBrowser.reload() } just Runs
        every { cefBrowser.stopLoad() } just Runs
        every { cefBrowser.openDevTools() } just Runs

        mockkConstructor(CefClientFactory::class)
        every { anyConstructed<CefClientFactory>().invoke() }.returns(cefClient)

        mockkStatic(CefApp::class)
        every { CefApp.getInstance().dispose() }.just(Runs)
        every { CefApp.getState() }.returns(CefApp.CefAppState.INITIALIZED)

        subject = WebViewPoolImplementation()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun find() {
        every { cefBrowser.find(any(), any(), any(), any()) } just Runs
        subject.component("1", "https://www.yahoo.co.jp")

        subject.find("1", "test", true)
        subject.find("1", "test", false)

        verify { cefBrowser.find(any(), true, any(), any()) }
        verify { cefBrowser.find(any(), false, any(), any()) }
    }

    @Test
    fun reload() {
        subject.component("1", "https://www.yahoo.co.jp")

        subject.reload("1")

        verify { cefBrowser.reload() }
    }

    @Test
    fun disposeDoNothingIfBrowsersIsNone() {
        subject.dispose("1")

        verify(inverse = true) { cefBrowser.close(any()) }
    }

    @Test
    fun dispose() {
        val component = subject.component("1", "https://www.yahoo.co.jp")

        assertNotNull(component)

        subject.dispose("1")

        verify { cefBrowser.close(any()) }
        verify { cefClient.doClose(any()) }
    }

    @Test
    fun disposeAllNoneCase() {
        subject.disposeAll()

        verify(inverse = true) { cefBrowser.close(any()) }
        verify(inverse = true) { cefClient.doClose(any()) }
        verify { CefApp.getInstance().dispose() }
        verify { cefClient.dispose() }
    }

    @Test
    fun disposeAll() {
        subject.component("1", "https://www.yahoo.co.jp")
        subject.disposeAll()

        verify { cefBrowser.close(any()) }
        verify { CefApp.getInstance().dispose() }
        verify { cefClient.doClose(any()) }
        verify { cefClient.dispose() }
    }

    @Test
    fun disposeAllPluralCase() {
        subject.component("1", "https://www.yahoo.co.jp")
        subject.component("2", "https://www.yahoo.co.jp")
        subject.component("3", "https://www.yahoo.co.jp")

        subject.disposeAll()

        verify(exactly = 3) { cefBrowser.close(any()) }
        verify { CefApp.getInstance().dispose() }
        verify { cefClient.dispose() }
    }

    @Test
    fun noopDisposeAll() {
        every { CefApp.getState() } returns CefApp.CefAppState.NONE

        subject.component("1", "https://www.yahoo.co.jp")
        subject.component("2", "https://www.yahoo.co.jp")
        subject.component("3", "https://www.yahoo.co.jp")

        subject.disposeAll()

        verify(inverse = true) { cefBrowser.close(any()) }
        verify(inverse = true) { CefApp.getInstance().dispose() }
        verify(inverse = true) { cefClient.dispose() }
    }

    @Test
    fun clearFind() {
        every { cefBrowser.stopFinding(any()) } just Runs

        subject.clearFind("test")

        verify { cefBrowser.stopFinding(any()) }
    }

    @Test
    fun switchDevTools() {
        subject.switchDevTools("test")

        verify { cefBrowser.openDevTools() }
    }

    @Test
    fun findId() {
        assertNull(subject.findId("test"))
        assertNull(subject.findId(mockk()))

        subject.component("1", "")

        assertEquals("1", subject.findId(cefBrowser))
    }

    @Test
    fun findIdWithNotContaining() {
        assertNull(subject.findId("test"))
        assertNull(subject.findId(mockk()))

        subject.component("1", "")

        assertNull(subject.findId(mockk<CefBrowser>()))
    }

}