package atwork.helpers

import atwork.controller.responses.Deductible.Payroll
import atwork.controller.responses.LoanType
import java.time.LocalDate

@Suppress("LongParameterList")
fun buildPayroll(
    type: LoanType,
    amountDeducibleThisMonth: Double = 100.0,
    dueDate: LocalDate = LocalDate.now(),
    totalInstallments: Int = 178,
    installmentsDeducted: Int = 48,
    totalAmountDeducted: Double = 3000.0,
    totalAmountRequested: Double = 13000.0
) = Payroll(
    type = type,
    dueDate = dueDate,
    symbol = "R$",
    totalInstallments = totalInstallments,
    amountDeductibleThisMonth = amountDeducibleThisMonth,
    installmentsDeducted = installmentsDeducted,
    totalAmountDeducted = totalAmountDeducted,
    totalAmountRequested = totalAmountRequested
)
