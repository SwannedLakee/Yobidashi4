package jp.toastkid.yobidashi4.presentation.markdown

import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TableSortServiceTest {

    private lateinit var subject: TableSortService

    @BeforeEach
    fun setUp() {
        subject = TableSortService()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val items = listOf(
            listOf("test", 1, 2.2),
            listOf("test2", -1, -1.2),
            listOf("test3", -1, "test"),
        )
        (0..2).forEach {
            subject.invoke(true, it, items)
            subject.invoke(true, it, items)
            subject.invoke(true, it, items)
            subject.invoke(false, it, items)
            subject.invoke(false, it, items)
            subject.invoke(false, it, items)
        }
        assertNull(subject.invoke(false, 2, emptyList()))
    }

}