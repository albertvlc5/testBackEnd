package atwork.gateway.payroll

import atwork.controller.responses.LoanType
import atwork.valueobject.Perk

data class LimitResponse(
    val maximumValue: Double
) {
    fun toPerk(): Perk = Perk(
        type = LoanType.PAYROLL,
        amountDeductible = null,
        totalAmountAvailable = maximumValue
    )
}
