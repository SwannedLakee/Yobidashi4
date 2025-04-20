package jp.toastkid.yobidashi4.domain.service.markdown

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.markdown.HorizontalRule
import jp.toastkid.yobidashi4.domain.model.markdown.ListLine
import jp.toastkid.yobidashi4.domain.model.markdown.TextBlock
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

class MarkdownParserTest {
    
    @InjectMockKs
    private lateinit var markdownParser: MarkdownParser

    @MockK
    private lateinit var path: Path
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { path.nameWithoutExtension } returns "test.md"

        mockkStatic(Files::class)
        every { Files.lines(path) } returns """
# Title
First description

## Image
![test](https://www.yahoo.co.jp/favicon.ico)

### Link
S&P 500 and [VIX](https://www.yahoo.co.jp/indices/VIX:IND)

## Quotation
> test
> quote

## List
- Aaron
- Beck
- Chief

## Ordered List
1. Soba
2. Udon
3. Tempra

## Task list
- [ ] Task1
- [x] Done

---
```Code fence```

## Table
| Time | Temperature
|:---|:---
| 8:10  | 29.4
| 8:25  | 29.5

## Sub
Test

## Code
```kotlin
println("Hello")
```

```kotlin:test
println("Hello")
```

## Table2
| Key | Value |
|:---|:---|
| First | 422
| Second | 123
| Third | 3442
| Tax | 121""".split("\n").stream()
    }
    
    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val markdown = markdownParser.invoke(path)

        val lines = markdown.lines()
        assertAll(
            { assertEquals("test", markdown.title()) },
            { assertTrue(lines.any { it is TextBlock }) },
            { assertTrue(lines.any { it is CodeBlockLine }) },
            { assertTrue(lines.any { it is HorizontalRule }) },
            { assertTrue(lines.any { it is ImageLine }) },
            {
                val tables = lines.filterIsInstance<TableLine>()
                assertEquals(2, tables.size)
                assertEquals(2, tables[0].header.size)
                assertEquals(2, tables[0].table.size)
                assertEquals(2, tables[1].header.size)
                assertEquals(4, tables[1].table.size)
            },
        )
    }

    @Test
    fun endWithListLineCase() {
        every { Files.lines(path) } returns """
# Title
First description

## List
- Aaron
- Beck
- Chief""".split("\n").stream()

        val markdown = markdownParser.invoke(path)

        assertTrue(markdown.lines().last() is ListLine)
        assertEquals(4, markdown.lines().size)
    }

    @Test
    fun exceptionCase() {
        every { Files.lines(path) } throws IOException()

        assertThrows<IOException> { markdownParser.invoke(path) }
    }

}