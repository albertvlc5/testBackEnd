package atwork.helpers

import atwork.controller.responses.Deductible.SalaryAdvancement
import atwork.controller.responses.LoanType.SALARY_ADVANCEMENT
import java.time.LocalDate

fun buildSalaryAdvance(
    amountDeducibleThisMonth: Double = 100.0,
    dueDate: LocalDate = LocalDate.now()
) = SalaryAdvancement(
    type = SALARY_ADVANCEMENT,
    amountDeductibleThisMonth = amountDeducibleThisMonth,
    symbol = "R$",
    dueDate = dueDate

)
