package jp.toastkid.yobidashi4.domain.service.aggregation

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import kotlin.io.path.nameWithoutExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EatingOutCounterServiceTest {

    @InjectMockKs
    private lateinit var aggregatorService: EatingOutCounterService

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension }.returns("test.md")

        mockkStatic(Files::class)
        every { Files.readAllLines(any()) }.returns(listOf("test content"))
        val lines =

        every { Files.readAllLines(any()) }.returns(
            """
## Text

## 家計簿
| 品目 | 金額 |
|:---|:---|
| (外食) マッシュルームとひき肉のカレー | 1000円
| 玉ねぎ8 | 218円
| にんじん | 129円
| いちごジャム | 118円
| Irregular Input | 18
| Empty Input | 円
| (外食) マッシュルームとひき肉のスパゲッティ | 1100円
| ユニバースターゴールド | 268円
""".split("\n").map { it.trim() }
        )

        MockKAnnotations.init(this)
        every { articlesReaderService.invoke() }.returns(Stream.of(path))
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val result = aggregatorService.invoke("test")

        verify(exactly = 1) { Files.readAllLines(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
        assertEquals(2, result.itemArrays().size)
        assertEquals(2100, result.sum())
    }
}