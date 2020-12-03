package atwork.gateway.payroll

import atwork.controller.responses.Deductible
import atwork.controller.responses.LoanType
import java.time.LocalDate
import java.util.UUID

data class DeductibleResponse(
    val id: UUID,
    val installment: Installment
) {
    val loanType: LoanTypeResponse by lazy { installment.loan.type }

    fun toDeductible(): Deductible {
        return when (loanType) {
            LoanTypeResponse.BALLOON, LoanTypeResponse.PRODUCT -> Deductible.Payroll(
                type = LoanType.PAYROLL_STORE,
                amountDeductibleThisMonth = installment.amount,
                dueDate = installment.dueDate,
                installmentsDeducted = installment.number,
                symbol = "R$",
                totalAmountDeducted = installment.number * installment.amount,
                totalAmountRequested = installment.loan.amount,
                totalInstallments = installment.loan.totalInstallments

            )
            LoanTypeResponse.DISCHARGING, LoanTypeResponse.REFINANCING, LoanTypeResponse.REQUESTING ->
                Deductible.Payroll(
                    type = LoanType.PAYROLL_CONSIGNADO,
                    amountDeductibleThisMonth = installment.amount,
                    dueDate = installment.dueDate,
                    installmentsDeducted = installment.number,
                    symbol = "R$",
                    totalAmountDeducted = installment.number * installment.amount,
                    totalAmountRequested = installment.loan.amount,
                    totalInstallments = installment.loan.totalInstallments
                )
            LoanTypeResponse.SALARY_ADVANCE -> Deductible.SalaryAdvancement(
                type = LoanType.SALARY_ADVANCEMENT,
                dueDate = installment.dueDate,
                amountDeductibleThisMonth = installment.amount,
                symbol = "R$"
            )
        }
    }
}

data class Installment(
    val id: UUID,
    val number: Int,
    val amount: Double,
    val dueDate: LocalDate,
    val loan: Loan
)

data class Loan(
    val id: UUID,
    val amount: Double,
    val requestDate: LocalDate,
    val type: LoanTypeResponse,
    val totalInstallments: Int,
    val externalId: String
)
