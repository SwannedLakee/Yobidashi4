package jp.toastkid.yobidashi4.infrastructure.service.article

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.Path

class ArticlesReaderServiceTest {

    @InjectMockKs
    private lateinit var articlesReaderService: ArticlesReaderServiceImplementation

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }
        MockKAnnotations.init(this)

        mockkStatic(Files::class)
        every { Files.list(any<Path>()) } returns mockk()
        every { setting.articleFolderPath() } returns mockk()

        mockkStatic(Path::class)
        every { Path.of(any<String>()) } returns mockk()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        articlesReaderService.invoke()

        verify(exactly = 1) { Files.list(any()) }
    }

}