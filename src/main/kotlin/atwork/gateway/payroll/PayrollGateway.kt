package atwork.gateway.payroll

import atwork.controller.responses.Deductible
import atwork.gateway.Authentication
import atwork.gateway.IDeductibleGateway
import atwork.gateway.IProductGateway
import atwork.valueobject.Cpf
import atwork.valueobject.Perk
import java.time.Month
import java.time.Year
import java.util.logging.Logger
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono

@Service
class PayrollGateway(
    @Value("\${payroll.baseUrl}") private val payrollBaseUrl: String,
    @Value("\${aang.baseUrl}") private val aangBaseUrl: String,
    private val httpClient: WebClient,
    private val authentication: Authentication.Payroll,
) : IProductGateway, IDeductibleGateway {
    override suspend fun getPerks(cpf: Cpf): Perk =
        httpClient.post()
            .uri("$payrollBaseUrl/partner/v1/payroll/simulate")
            .contentType(MediaType.APPLICATION_JSON)
            .headers { header -> header.setAll(authentication.getHeaders()) }
            .bodyValue(LimitRequest(cpf.number))
            .retrieve()
            .onStatus(
                { status -> status == HttpStatus.UNPROCESSABLE_ENTITY },
                { Mono.error(NonClientError("Client with CPF ${cpf.number} is not registered.")) }
            )
            .onStatus(
                { status -> status == HttpStatus.INTERNAL_SERVER_ERROR },
                {
                    val message = "Unexpected error with status code ${it.statusCode()} simulating " +
                        "Payroll loan for client with CPF $cpf."
                    Logger.getGlobal().severe(message)
                    Mono.error(SimulateUnknownError(message))
                }
            )
            .awaitBody<LimitResponse>()
            .toPerk()

    override suspend fun getDeductibles(cpf: Cpf, month: Month, year: Year): List<Deductible> =
        httpClient.get()
            .uri(
                "$aangBaseUrl/payroll-deductions?documentNumber={cpf}&month={month}&year={year}",
                cpf.number,
                month.value,
                year.value
            )
            .exchange()
            .flatMap {
                when (it.statusCode()) {
                    HttpStatus.OK -> it.bodyToMono(object : ParameterizedTypeReference<List<DeductibleResponse>>() {})
                    HttpStatus.NO_CONTENT -> Mono.just(emptyList())
                    else -> {
                        val exception = DeductibleUnknownError(it.statusCode(), cpf)
                        Logger.getGlobal().severe(exception.message)
                        Mono.error(exception)
                    }
                }
            }
            .awaitSingle()
            .map { it.toDeductible() }

    suspend fun getClientId(cpf: Cpf): String {
        return httpClient.get()
            .uri("$payrollBaseUrl/partner/v1/persons/{cpf}", cpf)
            .headers { header -> header.setAll(authentication.getHeaders()) }
            .retrieve()
            .onStatus(
                { status -> status == HttpStatus.UNPROCESSABLE_ENTITY || status == HttpStatus.NOT_FOUND },
                { Mono.error(NonClientError("Client with CPF ${cpf.number} is not registered.")) }
            )
            .onStatus(
                { status -> status == HttpStatus.INTERNAL_SERVER_ERROR },
                {
                    val message = "Unexpected error with status code ${it.statusCode()} getting " +
                        "Payroll persons information for client with CPF $cpf."
                    Logger.getGlobal().severe(message)
                    Mono.error(Throwable(message))
                }
            )
            .awaitBody<ClientIdResponse>().id
    }
}

class NonClientError(message: String) : Exception(message)
class SimulateUnknownError(message: String) : Exception(message)
class DeductibleUnknownError(statusCode: HttpStatus, cpf: Cpf) : Exception(
    "Unexpected error with status code $statusCode retrieving deductibles for client with CPF $cpf."
)
