package jp.toastkid.yobidashi4.domain.model.file

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.util.stream.Stream

class ArticleFilesFinderTest {

    @InjectMockKs
    private lateinit var subject: ArticleFilesFinder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Files::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test() {
        val path3 = mockk<Path>()
        every { path3.fileName.toString() } returns "『2021-01-02』"

        every { Files.list(any()) } answers {
            Stream.of(
                mockk<Path>().also { every { it.fileName.toString() } returns "2021-01-02" },
                mockk<Path>().also { every { it.fileName.toString() } returns "Test" },
                path3
            )
        }
        every { Files.getLastModifiedTime(any()) } returns FileTime.fromMillis(System.currentTimeMillis())

        val paths = subject.invoke(mockk())

        Assertions.assertEquals(2, paths.size)
    }

}