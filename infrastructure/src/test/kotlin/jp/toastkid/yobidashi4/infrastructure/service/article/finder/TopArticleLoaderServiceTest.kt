package jp.toastkid.yobidashi4.infrastructure.service.article.finder

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
import java.nio.file.attribute.FileTime
import java.util.stream.Stream
import kotlin.io.path.nameWithoutExtension

class TopArticleLoaderServiceTest {

    @InjectMockKs
    private lateinit var topArticleLoaderService: TopArticleLoaderServiceImplementation

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        every { setting.articleFolderPath() }.returns(mockk())

        mockkStatic(Files::class)
        val path1 = mockk<Path>()
        every { path1.nameWithoutExtension } returns "test.md"
        val path2 = mockk<Path>()
        every { path2.nameWithoutExtension } returns "test.jar"
        val path3 = mockk<Path>()
        every { path3.nameWithoutExtension } returns "test.txt"
        every { Files.list(any()) }.returns(Stream.of(path1, path2, path3))
        every { Files.getLastModifiedTime(any()) }.returns(FileTime.fromMillis(System.currentTimeMillis()))
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        topArticleLoaderService.invoke()

        verify { setting.articleFolderPath() }
        verify { Files.list(any()) }
        verify(exactly = 2) { Files.getLastModifiedTime(any()) }
    }

}