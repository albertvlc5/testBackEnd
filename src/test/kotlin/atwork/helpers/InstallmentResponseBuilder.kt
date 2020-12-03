package atwork.helpers

import atwork.gateway.payroll.InstallmentsResponse
import java.time.LocalDate
import java.util.UUID

@Suppress("LongParameterList")
fun buildInstallmentsResponse(
    id: String = UUID.randomUUID().toString(),
    paidAt: LocalDate? = null,
    dueDate: LocalDate,
    amount: Double = 100.0,
    status: String = "PAID",
    originalAmount: Double = 200.0,
    paidAmount: Double? = null
) = InstallmentsResponse(
    id = id,
    paidAt = paidAt,
    dueDate = dueDate,
    originalAmount = originalAmount,
    investor = InstallmentsResponse.Investor(id = "90", investor = "George Lucas"),
    paidAmount = paidAmount,
    number = 2,
    status = status,
    amount = amount
)
