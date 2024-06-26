package jp.toastkid.yobidashi4.presentation.loan

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.loan.PaymentDetail
import jp.toastkid.yobidashi4.presentation.loan.viewmodel.LoanCalculatorViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoanCalculatorViewKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(LoanCalculatorViewModel::class)
        every { anyConstructed<LoanCalculatorViewModel>().scheduleState() } returns listOf(
            PaymentDetail(1.0, 0.99, 2000),
            PaymentDetail(1.2, 1.0, 2000),
        )
        every { anyConstructed<LoanCalculatorViewModel>().loanAmount() } returns TextFieldValue("0")
        every { anyConstructed<LoanCalculatorViewModel>().loanTerm() } returns TextFieldValue("0")
        every { anyConstructed<LoanCalculatorViewModel>().interestRate() } returns TextFieldValue("0")
        every { anyConstructed<LoanCalculatorViewModel>().downPayment() } returns TextFieldValue("0")
        every { anyConstructed<LoanCalculatorViewModel>().managementFee() } returns TextFieldValue("0")
        every { anyConstructed<LoanCalculatorViewModel>().renovationReserves() } returns TextFieldValue("0")
        every { anyConstructed<LoanCalculatorViewModel>().roundToIntSafely(any()) } returns "0"
        every { anyConstructed<LoanCalculatorViewModel>().launch() } just Runs
        every { anyConstructed<LoanCalculatorViewModel>().listState() } returns LazyListState(0)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runDesktopComposeUiTest {
            setContent {
                LoanCalculatorView()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun firstVisibleItemIndexIsNotZero() {
        every { anyConstructed<LoanCalculatorViewModel>().listState() } returns LazyListState(1)

        runDesktopComposeUiTest {
            setContent {
                LoanCalculatorView()
            }
        }
    }

}