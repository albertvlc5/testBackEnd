package atwork.usecase

import atwork.Clock
import atwork.controller.responses.LoanType.PAYROLL
import atwork.extension.toCpf
import atwork.gateway.IDeductibleGateway
import atwork.helpers.buildPayroll
import atwork.helpers.buildSalaryAdvance
import atwork.shared.reset
import atwork.shared.setNow
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime
import java.time.Year
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class GetDeductiblesTest {

    private val deductibleGateway = mockk<IDeductibleGateway>()

    @AfterEach
    fun tearDown() {
        Clock.reset()
    }

    @Test
    fun `when getting deductibles and current day is previous 15th, get deductibles for current month`() {
        Clock.setNow(LocalDateTime.of(2020, 10, 12, 12, 0))
        val currentDate = Clock.getCurrentLocalDate()
        val cpf = "63202302400".toCpf()
        val deductiblesReturned = listOf(
            buildSalaryAdvance(dueDate = currentDate),
            buildPayroll(type = PAYROLL, dueDate = currentDate)
        )

        coEvery {
            deductibleGateway.getDeductibles(cpf, currentDate.month, Year.of(currentDate.year))
        } returns deductiblesReturned

        runBlocking {
            val response = GetDeductibles(deductibleGateway)(cpf)

            assertThat(response).isEqualTo(deductiblesReturned)
            coVerify(exactly = 1) {
                deductibleGateway.getDeductibles(cpf, currentDate.month, Year.of(currentDate.year))
            }
        }
    }

    @Test
    fun `when getting deductibles and current day is after or equals 15th, get deductibles for the next month`() {
        Clock.setNow(LocalDateTime.of(2020, 10, 16, 12, 0))
        val currentDate = Clock.getCurrentLocalDate()
        val cpf = "63202302400".toCpf()
        val deductiblesReturned = listOf(
            buildSalaryAdvance(dueDate = currentDate.plusMonths(1)),
            buildPayroll(type = PAYROLL, dueDate = currentDate.plusMonths(1))
        )

        coEvery {
            deductibleGateway.getDeductibles(
                cpf,
                currentDate.plusMonths(1).month,
                Year.of(currentDate.plusMonths(1).year)
            )
        } returns deductiblesReturned

        runBlocking {
            val response = GetDeductibles(deductibleGateway)(cpf)

            assertThat(response).isEqualTo(deductiblesReturned)
            coVerify(exactly = 1) {
                deductibleGateway.getDeductibles(
                    cpf,
                    currentDate.plusMonths(1).month,
                    Year.of(currentDate.plusMonths(1).year)
                )
            }
        }
    }
}
