package atwork.gateway.authentication

import atwork.gateway.UnauthorizedException
import atwork.controller.JWTToken
import atwork.valueobject.UserInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.core.publisher.Mono
import java.util.logging.Logger

@Service
class AuthenticationGateway(
    @Value("\${platform.authentication.baseUrl}") private val baseUrl: String,
    private val httpClient: WebClient,
) {
    suspend fun getUserInfo(token: JWTToken): UserInfo {
        return httpClient.get()
            .uri("$baseUrl/accounts/current?includes=authenticationClaims,customer")
            .headers { header ->
                header["accept"] = "application/vnd.creditas.v1+json"
                header["authorization"] = "Bearer $token"
            }
            .retrieve()
            .onStatus(
                { it != HttpStatus.OK },
                {
                    if (it.statusCode() == HttpStatus.UNAUTHORIZED) {
                        val message = "Token is unauthorized to talk with Authentication service."
                        Logger.getGlobal().info(message)
                        Mono.error(UnauthorizedException(message))
                    } else {
                        val message = "Unexpected error with status code ${it.statusCode()} getting user " +
                            "info from Authentication service."
                        Logger.getGlobal().severe(message)
                        Mono.error(Throwable(it.statusCode().toString()))
                    }
                }
            )
            .awaitBody<UserAccountResponse>()
            .toUserInfo()
    }
}
