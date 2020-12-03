package atwork.controller

import java.time.Month
import java.time.Year
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

open class HttpStub(
    val path: String,
    val responseBody: String? = null,
    val responseCode: Int = HttpStatus.OK.value(),
    val httpMethod: HttpMethod = HttpMethod.GET,
)

class UserAccountHttpStub(
    responseBody: String? = null,
    responseCode: Int = HttpStatus.OK.value(),
) : HttpStub(
    "/auth/accounts/current?includes=authenticationClaims,customer",
    responseBody,
    responseCode,
    HttpMethod.GET
)

class PayrollPersonsHttpStub(
    cpf: String,
    responseBody: String? = null,
    responseCode: Int = HttpStatus.OK.value(),
) : HttpStub("/partner/v1/persons/$cpf", responseBody, responseCode, HttpMethod.GET)

class PayrollLoansHttpStub(
    clientId: String,
    responseBody: String? = null,
    responseCode: Int = HttpStatus.OK.value(),
) : HttpStub("/partner/v1/loans?clientId=$clientId", responseBody, responseCode, HttpMethod.GET)

class PayrollInstallmentsHttpStub(
    contractId: String,
    responseBody: String? = null,
    responseCode: Int = HttpStatus.OK.value(),
) : HttpStub("/partner/v1/loans/$contractId/installments", responseBody, responseCode, HttpMethod.GET)

class AangDeductiblesHttpStub(
    cpf: String,
    month: Month,
    year: Year,
    responseBody: String? = null,
    responseCode: Int = HttpStatus.OK.value()
) : HttpStub(
    "/payroll-deductions?documentNumber=$cpf&month=${month.value}&year=${year.value}",
    responseBody,
    responseCode,
    HttpMethod.GET
)
