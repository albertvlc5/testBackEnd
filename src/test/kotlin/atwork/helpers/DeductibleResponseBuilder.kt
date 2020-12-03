package atwork.helpers

import atwork.gateway.payroll.DeductibleResponse
import atwork.gateway.payroll.Installment
import atwork.gateway.payroll.Loan
import atwork.gateway.payroll.LoanTypeResponse
import java.time.LocalDate
import java.util.UUID

fun buildDeductibleResponse(id: UUID = UUID.randomUUID(), installment: Installment = buildInstallment()) =
    DeductibleResponse(id, installment)

fun buildInstallment(
    id: UUID = UUID.randomUUID(),
    number: Int = 1,
    amount: Double = 50.0,
    dueDate: LocalDate = LocalDate.of(2020, 10, 10),
    loan: Loan = buildLoan()
) = Installment(
    id = id,
    number = number,
    amount = amount,
    dueDate = dueDate,
    loan = loan
)

fun buildLoan(
    id: UUID = UUID.randomUUID(),
    amount: Double = 1200.00,
    requestDate: LocalDate = LocalDate.of(2020, 8, 10),
    type: LoanTypeResponse = LoanTypeResponse.BALLOON,
    totalInstallments: Int = 24
) = Loan(
    id = id,
    amount = amount,
    requestDate = requestDate,
    type = type,
    totalInstallments = totalInstallments,
    externalId = "12352"
)
