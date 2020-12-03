package atwork.gateway.salaryadvance

import atwork.gateway.IProductGateway
import atwork.gateway.payroll.PayrollGateway
import atwork.valueobject.Cpf
import atwork.valueobject.Perk
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono
import java.util.logging.Logger

@Service
class SalaryAdvanceGateway(
    @Value("\${salary-advance.baseUrl}") private val baseUrl: String,
    private val httpClient: WebClient,
    private val payrollGateway: PayrollGateway,
) : IProductGateway {
    override suspend fun getPerks(cpf: Cpf): Perk {
        val clientId = payrollGateway.getClientId(cpf)
        return httpClient.get()
            .uri("$baseUrl/salary-advance/client/{clientId}/summary", clientId)
            .retrieve()
            .onStatus(
                { status -> status == HttpStatus.INTERNAL_SERVER_ERROR },
                {
                    val message = "Unexpected error with status code ${it.statusCode()} getting " +
                        "Salary Advance summary for client with id $clientId."
                    Logger.getGlobal().severe(message)
                    Mono.error(Throwable(message))
                }
            )
            .awaitBody<SummaryResponse>().toDomain()
    }
}
