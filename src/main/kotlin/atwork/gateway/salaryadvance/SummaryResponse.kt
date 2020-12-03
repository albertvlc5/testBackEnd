package atwork.gateway.salaryadvance

import atwork.controller.responses.LoanType.SALARY_ADVANCEMENT
import atwork.valueobject.Perk

data class SummaryResponse(
    val amountAdvancedInCurrentCycle: Double,
    val amountAvailableToAdvanceToday: Double
) {
    fun toDomain(): Perk = Perk(
        type = SALARY_ADVANCEMENT,
        amountDeductible = amountAdvancedInCurrentCycle,
        totalAmountAvailable = amountAvailableToAdvanceToday
    )
}
