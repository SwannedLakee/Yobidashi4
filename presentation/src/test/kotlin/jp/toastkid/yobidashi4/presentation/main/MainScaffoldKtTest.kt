package jp.toastkid.yobidashi4.presentation.main

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.tab.BarcodeToolTab
import jp.toastkid.yobidashi4.domain.model.tab.FileRenameToolTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.service.archive.KeywordArticleFinder
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MainScaffoldKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var keywordArticleFinder: KeywordArticleFinder

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { keywordArticleFinder } bind(KeywordArticleFinder::class)
                    single(qualifier=null) { articlesReaderService } bind(ArticlesReaderService::class)
                }
            )
        }

        every { mainViewModel.snackbarHostState() } returns SnackbarHostState()
        every { mainViewModel.showBackgroundImage() } returns false
        every { mainViewModel.showWebSearch() } returns false
        every { mainViewModel.showAggregationBox() } returns false
        every { mainViewModel.openFind() } returns false
        every { mainViewModel.showInputBox() } returns false
        every { mainViewModel.openMemoryUsageBox() } returns false
        every { mainViewModel.slideshowPath() } returns mockk()
        every { mainViewModel.closeSlideshow() } just Runs
        every { mainViewModel.loadBackgroundImage() } just Runs
        every { mainViewModel.openArticleList() } returns false
        every { mainViewModel.articles() } returns emptyList()
        every { mainViewModel.reloadAllArticle() } just Runs
        every { mainViewModel.tabs } returns mutableStateListOf<Tab>()

    /*    mockkConstructor(TextFileReceiver::class, SlideshowWindow::class)
        every { anyConstructed<TextFileReceiver>().launch() } just Runs
        every { anyConstructed<SlideshowWindow>().openWindow(any(), any()) } just Runs*/
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun mainScaffold() {
        runDesktopComposeUiTest {
            setContent {
                MainScaffold()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun useOptionalComponents() {
        every { mainViewModel.backgroundImage() } returns ImageBitmap(1, 1)
        every { mainViewModel.showBackgroundImage() } returns true
        every { mainViewModel.showWebSearch() } returns true
        every { mainViewModel.showAggregationBox() } returns true
        every { mainViewModel.openFind() } returns true
        every { mainViewModel.showInputBox() } returns true
        every { mainViewModel.openMemoryUsageBox() } returns true
        every { mainViewModel.currentTab() } returns mockk<MarkdownPreviewTab>()
        every { mainViewModel.initialAggregationType() } returns 0
        every { mainViewModel.inputValue() } returns TextFieldValue("search")
        every { mainViewModel.findStatus() } returns "test"
        every { mainViewModel.caseSensitive() } returns true

        runDesktopComposeUiTest {
            setContent {
                MainScaffold()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun useTabContents() {
        every { mainViewModel.selected } returns mutableStateOf(0)
        every { mainViewModel.currentTab() } returns LoanCalculatorTab()
        every { mainViewModel.tabs } returns mutableStateListOf(
            LoanCalculatorTab(),
            BarcodeToolTab(),
            FileRenameToolTab()
        )

        runDesktopComposeUiTest {
            setContent {
                MainScaffold()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun useFileRenameToolTabContents() {
        every { mainViewModel.selected } returns mutableStateOf(2)
        every { mainViewModel.currentTab() } returns FileRenameToolTab()
        every { mainViewModel.droppedPathFlow() } returns emptyFlow()
        every { mainViewModel.showSnackbar(any(), any(), any()) } just Runs
        every { mainViewModel.tabs } returns mutableStateListOf(
            LoanCalculatorTab(),
            BarcodeToolTab(),
            FileRenameToolTab()
        )

        runDesktopComposeUiTest {
            setContent {
                MainScaffold()
            }
        }
    }

}