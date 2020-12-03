package atwork.gateway.payroll

import atwork.Clock
import atwork.helpers.buildInstallmentsResponse
import atwork.shared.reset
import atwork.shared.setNow
import java.time.LocalDate
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class InstallmentsResponseTest {

    @AfterEach
    fun tearDown() {
        Clock.reset()
    }

    @Test
    fun `Next installment for current month, return false`() {
        Clock.setNow(LocalDateTime.of(2020, 10, 5, 12, 0, 0))
        val currentMonthInstallment = buildInstallmentsResponse(dueDate = LocalDate.of(2020, 10, 2))

        assertThat(currentMonthInstallment.isNextInstallment()).isFalse
    }

    @Test
    fun `Next installment for next month installment, return true`() {
        Clock.setNow(LocalDateTime.of(2020, 10, 5, 12, 0, 0))
        val nextMonthInstallment = buildInstallmentsResponse(dueDate = LocalDate.of(2020, 11, 2), status = "OPEN")

        assertThat(nextMonthInstallment.isNextInstallment()).isTrue
    }

    @Test
    fun `Next installment for next two month installment, return false`() {
        Clock.setNow(LocalDateTime.of(2020, 10, 5, 12, 0, 0))
        val twoMonthInstallment = buildInstallmentsResponse(dueDate = LocalDate.of(2020, 12, 2), status = "OPEN")

        assertThat(twoMonthInstallment.isNextInstallment()).isFalse
    }

    @Test
    fun `Next installment for next month and due date day equals to current day, return true`() {
        Clock.setNow(LocalDateTime.of(2020, 10, 5, 12, 0, 0))
        val nextMonthInstallment = buildInstallmentsResponse(dueDate = LocalDate.of(2020, 11, 5), status = "OPEN")

        assertThat(nextMonthInstallment.isNextInstallment()).isTrue
    }

    @Test
    fun `Next installment for next year same month, return false`() {
        Clock.setNow(LocalDateTime.of(2020, 10, 5, 12, 0, 0))
        val nextMonthInstallment = buildInstallmentsResponse(dueDate = LocalDate.of(2021, 11, 5), status = "OPEN")

        assertThat(nextMonthInstallment.isNextInstallment()).isFalse
    }
}
