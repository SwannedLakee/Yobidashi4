package jp.toastkid.yobidashi4.presentation.loan.viewmodel

import androidx.compose.ui.text.input.VisualTransformation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoanCalculatorViewModelTest {

    private lateinit var subject: LoanCalculatorViewModel

    @BeforeEach
    fun setUp() {
        subject = LoanCalculatorViewModel()

        subject.launch()
    }

    @Test
    fun result() {
        assertTrue(subject.result().isEmpty())
    }

    @Test
    fun loanAmount() {
        subject.setLoanAmount("20000000")

        assertEquals("20000000", subject.loanAmount())
    }

    @Test
    fun loanTerm() {
        subject.setLoanTerm("5")

        assertEquals("5", subject.loanTerm())
    }

    @Test
    fun interestRate() {
        subject.setInterestRate("0.5")

        assertEquals("0.5", subject.interestRate())
    }

    @Test
    fun downPayment() {
        subject.setDownPayment("2000000")

        assertEquals("2000000", subject.downPayment())
    }

    @Test
    fun managementFee() {
        subject.setManagementFee("30000")

        assertEquals("30000", subject.managementFee())
    }

    @Test
    fun renovationReserves() {
        subject.setRenovationReserves("30001")

        assertEquals("30001", subject.renovationReserves())
    }

    @Test
    fun roundToIntSafely() {
        assertEquals("0", subject.roundToIntSafely(Double.NaN))
        assertEquals("2", subject.roundToIntSafely(2.2))
        assertEquals("5", subject.roundToIntSafely(4.5))
    }

    @Test
    fun inputChannel() {
        assertNotNull(subject.inputChannel())
    }

    @Test
    fun setEmpty() {
        subject.setRenovationReserves("")

        assertEquals("0", subject.renovationReserves())
    }

    @Test
    fun setIncludingComma() {
        subject.setRenovationReserves("10,000")

        assertEquals("10000", subject.renovationReserves())
    }

    @Test
    fun visualTransformation() {
        val visualTransformation = subject.visualTransformation()

        assertNotSame(VisualTransformation.None, visualTransformation)
    }

    @Test
    fun listState() {
        assertEquals(0, subject.listState().firstVisibleItemIndex)
    }

}