package jp.toastkid.yobidashi4.domain.service.tool.compound

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CompoundInterestCalculatorServiceTest {

    private lateinit var compoundInterestCalculatorService: CompoundInterestCalculatorService

    @BeforeEach
    fun setUp() {
        compoundInterestCalculatorService = CompoundInterestCalculatorService()
    }

    @Test
    fun test() {
        val input = CompoundInterestCalculatorInput.from("0.0", "120000", "0.01", "10") ?: return fail()
        val result = compoundInterestCalculatorService.invoke(input)
        assertEquals(3, result.header().size)
        assertEquals(10, result.itemArrays().size)
        assertEquals(Int::class.java, result.columnClass(0))
        assertEquals(Long::class.java, result.columnClass(1))
        assertEquals(Long::class.java, result.columnClass(2))
        assertNotNull(result.title())
        assertFalse(result.isEmpty())

        val tenYearsLater = result.get(10) ?: fail("This case does not allow null.")
        assertEquals(1212000, tenYearsLater.second)
        assertEquals(1255466, tenYearsLater.third)
    }

    @Test
    fun nanCase() {
        val input = CompoundInterestCalculatorInput.from("0.0", "0", "NaN", "10") ?: return fail()
        val result = compoundInterestCalculatorService.invoke(input)
        assertEquals(3, result.header().size)
        assertEquals(0, result.itemArrays().size)
        assertNotNull(result.title())
        assertTrue(result.isEmpty())
    }

}