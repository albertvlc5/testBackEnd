package atwork.gateway.payroll

import atwork.Clock.getCurrentLocalDate
import java.time.LocalDate

data class InstallmentsResponse(
    val id: String,
    val number: Int,
    val originalAmount: Double,
    val amount: Double,
    val paidAt: LocalDate?,
    val paidAmount: Double?,
    val dueDate: LocalDate,
    val status: String,
    val investor: Investor,
) {
    data class Investor(
        val id: String,
        val investor: String,
    )

    fun isNextInstallment(): Boolean {
        val dateNextInstallment = getCurrentLocalDate().plusMonths(1)
        return dueDate.month == dateNextInstallment.month && dueDate.year == dateNextInstallment.year
    }

    fun isPaid() = status == "PAID"
}
