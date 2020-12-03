package atwork.gateway.payroll

import atwork.Clock
import atwork.helpers.buildInstallmentsResponse
import atwork.shared.setNow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class InstallmentResponseTest {

    @AfterEach
    fun resetClock() = Clock.setNow(LocalDateTime.now())

    @Test
    fun `when dueDate is between next month and 2 month in the future, isNextInstallment returns true`() {
        val installment = buildInstallmentsResponse(
            id = "123",
            dueDate = LocalDate.now().plusMonths(1).plusDays(3),
            paidAt = null
        )

        assertThat(installment.isNextInstallment()).isTrue()
    }

    @Test
    fun `when dueDate is today, isNextInstallment returns false`() {
        val installment = buildInstallmentsResponse(
            id = "123",
            dueDate = LocalDate.now(),
            paidAt = null
        )

        assertThat(installment.isNextInstallment()).isFalse()
    }

    @Test
    fun `when dueDate is more than two months in the future, isNextInstallment returns false`() {
        val installment = buildInstallmentsResponse(
            id = "123",
            dueDate = LocalDate.now().plusMonths(3),
            paidAt = null
        )

        assertThat(installment.isNextInstallment()).isFalse()
    }
}
