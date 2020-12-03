package atwork.helpers

import atwork.controller.responses.LoanType
import atwork.valueobject.Perk

fun buildPerk(
    type: LoanType,
    amountDeductible: Double = 9812.3,
    totalAmountAvailable: Double = 335.6
) = Perk(
    type = type,
    amountDeductible = amountDeductible,
    totalAmountAvailable = totalAmountAvailable,
    symbol = "R$"
)
