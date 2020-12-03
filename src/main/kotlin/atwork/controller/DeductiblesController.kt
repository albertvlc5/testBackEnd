package atwork.controller

import atwork.controller.responses.Deductible
import atwork.controller.responses.Deductible.Payroll
import atwork.controller.responses.Deductible.SalaryAdvancement
import atwork.gateway.authentication.AuthenticationGateway
import atwork.presenter.DeductiblesPresenter
import com.newrelic.api.agent.Trace
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DeductiblesController(
    private val authenticationGateway: AuthenticationGateway,
    private val deductiblesPresenter: DeductiblesPresenter
) {

    @DeductiblesControllerDocs
    @GetMapping("/deductibles")
    @Trace(dispatcher = true)
    suspend fun getDeductibles(
        @RequestHeader("Authorization") token: JWTToken
    ): ResponseEntity<Mono<List<Deductible>>> {
        val userInformation = authenticationGateway.getUserInfo(token)
        val data = deductiblesPresenter.getAllDeductibles(userInformation.cpf)
            .map {
                if (it is Payroll) {
                    Payroll(
                        it.type,
                        it.amountDeductibleThisMonth,
                        it.symbol,
                        it.dueDate.plusMonths(1),
                        it.totalAmountDeducted,
                        it.totalAmountRequested,
                        it.totalInstallments,
                        it.installmentsDeducted
                    )
                } else {
                    SalaryAdvancement(it.type, it.amountDeductibleThisMonth, it.symbol, it.dueDate.plusMonths(1))
                }
            }
        return ok(Mono.just(data))
    }

    @DeductiblesControllerDocs
    @GetMapping("/v2/deductibles")
    @Trace(dispatcher = true)
    suspend fun getDeductiblesV2(
        @RequestHeader("Authorization") token: JWTToken
    ): ResponseEntity<Mono<List<Deductible>>> {
        val userInformation = authenticationGateway.getUserInfo(token)
        val data = deductiblesPresenter.getAllDeductibles(userInformation.cpf)
        return ok(Mono.just(data))
    }
}
