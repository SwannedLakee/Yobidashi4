package jp.toastkid.yobidashi4.infrastructure.model.browser

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.infrastructure.service.CefClientFactory
import kotlin.test.assertNotNull
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BrowserPoolImplementationTest {

    private lateinit var browserPoolImplementation: BrowserPoolImplementation

    @MockK
    private lateinit var cefClient: CefClient

    @MockK
    private lateinit var cefBrowser: CefBrowser

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { cefClient.createBrowser(any(), any(), any()) }.returns(cefBrowser)
        every { cefBrowser.uiComponent }.returns(mockk())
        every { cefBrowser.close(any()) }.just(Runs)

        mockkConstructor(CefClientFactory::class)
        every { anyConstructed<CefClientFactory>().invoke() }.returns(cefClient)

        browserPoolImplementation = BrowserPoolImplementation()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onLayout() {
    }

    @Test
    fun component() {
    }

    @Test
    fun devTools() {
    }

    @Test
    fun find() {
    }

    @Test
    fun reload() {
    }

    @Test
    fun disposeDoNothingIfBrowsersIsNone() {
        browserPoolImplementation.dispose("1")

        verify(inverse = true) { cefBrowser.close(any()) }
    }

    @Test
    fun dispose() {
        val component = browserPoolImplementation.component("1", "https://www.yahoo.co.jp")

        assertNotNull(component)

        browserPoolImplementation.dispose("1")

        verify { cefBrowser.close(any()) }
    }

    @Test
    fun disposeAll() {
    }
}