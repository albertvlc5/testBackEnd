package atwork.helpers

import atwork.gateway.payroll.LoanResponse
import atwork.gateway.payroll.LoanTypeResponse
import atwork.gateway.payroll.LoanTypeResponse.BALLOON
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun buildLoanResponse(
    id: String = "1234",
    contractId: String = "12345",
    type: LoanTypeResponse = BALLOON,
    requestedAmount: Double = 5563.43,
    totalInstallments: Int = 12
) = LoanResponse(
    id = id,
    contractId = contractId,
    type = type,
    ccb = "1239",
    companyCnpj = "LKJS-123",
    companyName = "LUCASFILM",
    interestRate = 1.0,
    releaseDate = LocalDateTime.parse("2020-12-01 09:08:01", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    requestedAmount = requestedAmount,
    totalInstallments = totalInstallments
)
