package jp.toastkid.yobidashi4.domain.service.aggregation

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.nameWithoutExtension

internal class OutgoAggregatorServiceTest {

    @InjectMockKs
    private lateinit var outgoAggregatorService: OutgoAggregatorService

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension } returns "2024-03-18.md"

        mockkStatic(Files::class)
        val lines = """
_
## 家計簿_
| 品目 | 金額 |_
|:---|:---|_
| (外食) マッシュルームとひき肉のカレー | 1000円_
| 玉ねぎ8 | 218円_
| にんじん | 129円_
| いちごジャム | 118円_
| Lack of price | 円_
| Lack of unit | 118_
| ユニバースターゴールド | 268円_
_
""".split("_").map { it.trim() }

        every { Files.readAllLines(any()) }.returns(lines)

        MockKAnnotations.init(this)
        every { articlesReaderService.invoke() }.returns(Stream.of(path))
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        val outgoAggregationResult = outgoAggregatorService.invoke("2024-03")

        assertEquals(1733, outgoAggregationResult.sum())

        verify(exactly = 1) { Files.readAllLines(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
    }

    @Test
    fun monthlyCase() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension }.returns("2024-02-23.md")

        val lines = """
_
## 家計簿_
| 品目 | 金額 |_
|:---|:---|_
| (外食) マッシュルームとひき肉のカレー | 1000円_
| 玉ねぎ8 | 218円_
"""
            .split("_").map { it.trim() }
        every { Files.readAllLines(any()) }.returns(lines)
        every { articlesReaderService.invoke() }.returns(Stream.of(path))

        val outgoAggregationResult = outgoAggregatorService.invoke("2024")

        assertEquals(1218, outgoAggregationResult.sum())
        assertEquals("2024-02", outgoAggregationResult.itemArrays().first()[0])

        verify(exactly = 1) { Files.readAllLines(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
    }

}