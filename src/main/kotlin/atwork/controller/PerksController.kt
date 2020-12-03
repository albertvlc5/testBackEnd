package atwork.controller

import atwork.gateway.authentication.AuthenticationGateway
import atwork.presenter.PerkPresenter
import atwork.valueobject.Perk
import com.newrelic.api.agent.Trace
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping(path = ["/perks"], produces = [MediaType.APPLICATION_JSON_VALUE])
class PerksController(
    private val authenticationGateway: AuthenticationGateway,
    private val perkPresenter: PerkPresenter
) {

    @PerksControllerDocs
    @GetMapping
    @Trace(dispatcher = true)
    suspend fun getPerks(
        @RequestHeader("Authorization") token: JWTToken
    ): ResponseEntity<Mono<List<Perk>>> {
        val userInformation = authenticationGateway.getUserInfo(token)

        val data = perkPresenter.getAllPerks(userInformation.cpf)

        return ResponseEntity.ok(Mono.just(data))
    }
}
