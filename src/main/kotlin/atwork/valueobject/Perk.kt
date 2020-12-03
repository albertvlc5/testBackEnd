package atwork.valueobject

import atwork.controller.responses.LoanType

data class Perk(
    val type: LoanType,
    val amountDeductible: Double?,
    val totalAmountAvailable: Double,
    val symbol: String = "R$"
)
