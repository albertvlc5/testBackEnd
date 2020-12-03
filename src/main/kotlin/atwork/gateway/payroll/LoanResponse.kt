package atwork.gateway.payroll

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class LoanResponse(
    val id: String,
    val contractId: String,
    val requestedAmount: Double,
    val totalInstallments: Int,
    val type: LoanTypeResponse,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val releaseDate: LocalDateTime,
    val ccb: String?,
    val interestRate: Double,
    val companyCnpj: String,
    val companyName: String
)
