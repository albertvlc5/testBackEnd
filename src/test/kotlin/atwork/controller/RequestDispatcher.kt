package atwork.controller

import atwork.extension.toJson
import atwork.gateway.authentication.Customer
import atwork.gateway.authentication.UserAccountResponse
import atwork.gateway.payroll.ClientIdResponse
import atwork.gateway.payroll.InstallmentsResponse
import atwork.gateway.payroll.LimitResponse
import atwork.gateway.payroll.LoanTypeResponse.BALLOON
import atwork.gateway.payroll.LoanTypeResponse.REQUESTING
import atwork.gateway.payroll.LoanTypeResponse.SALARY_ADVANCE
import atwork.gateway.salaryadvance.SummaryResponse
import atwork.helpers.buildLoanResponse
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.time.LocalDate
import java.util.UUID

class RequestDispatcher : Dispatcher() {
    private val cpf = "31795002042"
    private val clientIdResponse = ClientIdResponse(id = "client123")
    private val salaryAdvanceResponse = SummaryResponse(
        amountAvailableToAdvanceToday = 888.20,
        amountAdvancedInCurrentCycle = 99.9
    )
    private val userAccountResponse = buildUserAccountResponse()
    private val firstLoanResponse = buildLoanResponse(
        type = BALLOON,
        requestedAmount = 623.10
    )
    private val secondLoanResponse = buildLoanResponse(
        type = REQUESTING,
        requestedAmount = 500.30
    )
    private val thirdLoanResponse = buildLoanResponse(
        type = SALARY_ADVANCE,
        requestedAmount = 500.30,
    )
    private val installmentResponse = InstallmentsResponse(
        id = "installment123",
        paidAt = LocalDate.now(),
        dueDate = LocalDate.parse("2020-11-03"),
        originalAmount = 200.0,
        investor = InstallmentsResponse.Investor(id = "90", investor = "George Lucas"),
        paidAmount = 100.0,
        number = 2,
        status = "PAID",
        amount = 999.78
    )
    val limitResponse = LimitResponse(maximumValue = 976.21)

    override fun dispatch(request: RecordedRequest): MockResponse =
        when (request.path) {
            "/auth/accounts/current?includes=authenticationClaims,customer" ->
                MockResponse()
                    .setBody(userAccountResponse.toJson())
                    .setHeader("Content-Type", "application/json")
            "/partner/v1/loans?clientId=${clientIdResponse.id}" ->
                MockResponse().setBody(listOf(firstLoanResponse, secondLoanResponse, thirdLoanResponse).toJson())
                    .setHeader("Content-Type", "application/json")
            "/partner/v1/persons/$cpf" ->
                MockResponse().setBody(clientIdResponse.toJson())
                    .setHeader("Content-Type", "application/json")
            "/partner/v1/loans/12345/installments" ->
                MockResponse().setBody(listOf(installmentResponse).toJson())
                    .setHeader("Content-Type", "application/json")
            "/salary-advance/client/${clientIdResponse.id}/summary" ->
                MockResponse().setBody(salaryAdvanceResponse.toJson())
                    .setHeader("Content-Type", "application/json")
            "/partner/v1/payroll/simulate" ->
                MockResponse()
                    .setBody(limitResponse.toJson())
                    .setHeader("Content-Type", "application/json")
            else -> MockResponse().setResponseCode(500)
        }

    private fun buildUserAccountResponse(
        id: UUID = UUID.randomUUID(),
        screenName: String = "Test User",
        customerId: UUID = UUID.randomUUID(),
        authenticationClaims: List<Map<String, String>> = listOf(
            mapOf(

                "id" to "eed34b38-3677-41f9-86b2-eaf31417a363",
                "type" to "EMAIL",
                "emailAddress" to "test@mail.com",
                "status" to "VERIFIED"

            ),
            mapOf(
                "id" to "298ff648-f0f5-4bd3-b003-6c7e71511db7",
                "type" to "MAIN_DOCUMENT",
                "documentCode" to cpf,
                "documentType" to "CPF",
                "status" to "VERIFIED"
            )
        )
    ) = UserAccountResponse(
        id = id.toString(),
        screenName = screenName,
        customer = Customer(customerId.toString()),
        authenticationClaims = authenticationClaims
    )
}
