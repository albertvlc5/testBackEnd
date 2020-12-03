package atwork.configuration

import authentication.CreditasAuthConfigProperties
import authentication.CreditasAuthenticationContext
import authentication.CreditasAuthenticationProvider
import authentication.JWTAuthentication
import authentication.JWTValidationService
import authentication.SidecarProxyAuthentication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.logging.Logger

@EnableWebFluxSecurity
class Security {
    @Bean
    open fun securitygWebFilterChain(
        http: ServerHttpSecurity,
        jwtAuthenticationManager: ReactiveAuthenticationManager,
        jwtAuthenticationConverter: ServerAuthenticationConverter
    ): SecurityWebFilterChain {
        val authenticationWebFilter = AuthenticationWebFilter(jwtAuthenticationManager)
        authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter)

        return http
            .csrf()
            .disable()
            .cors()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
            .and()
            .authorizeExchange()
            .pathMatchers(
                "/health",
                "/metrics/**",
                "/prometheus",
                "/webjars/**",
                "/v3/api-docs/**",
                "/swagger-ui.html",
            )
            .permitAll()
            .anyExchange().authenticated()
            .and().addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .formLogin().disable()
            .httpBasic().disable()
            .build()
    }
}

@Component
class JwtServerAuthenticationConverter(private val creditasAuthConfigProperties: CreditasAuthConfigProperties) :
    ServerAuthenticationConverter {
    companion object {
        private const val HEADER: String = "X-Auth-User"
        private const val USER_ID_HEADER = "X-User-Id"
        private const val USER_TYPE_HEADER = "X-User-Type"
    }

    init {
        if (creditasAuthConfigProperties.devModeEnabled) {
            Logger.getGlobal().warning("INSECURE AUTHENTICATION JWT DEV MODE IS ENABLED")
        }
    }

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        return Mono.justOrEmpty(exchange)
            .flatMap { Mono.justOrEmpty(processHeaders(exchange.request)) }
    }

    @Suppress("ReturnCount")
    private fun processHeaders(request: ServerHttpRequest): UsernamePasswordAuthenticationToken? {
        return if (creditasAuthConfigProperties.devModeEnabled && isFromSidecarProxy(request)) {
            val userId = request.headers[USER_ID_HEADER]?.firstOrNull() ?: return null
            val userType = request.headers[USER_TYPE_HEADER]?.firstOrNull() ?: return null
            UsernamePasswordAuthenticationToken(userId, userType)
        } else {
            val authUser = request.headers[HEADER]?.firstOrNull() ?: return null
            UsernamePasswordAuthenticationToken(authUser, authUser)
        }
    }

    private fun isFromSidecarProxy(request: ServerHttpRequest) =
        request.remoteAddress!!.address == request.localAddress!!.address
}

@Component
class JwtAuthenticationManager(
    private val jwtValidation: JWTValidationService,
    private val creditasAuthConfigProperties: CreditasAuthConfigProperties
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val auth = if (creditasAuthConfigProperties.devModeEnabled) {
            authenticationDevMode(authentication)
        } else {
            authenticationMode(authentication)
        }

        auth.isAuthenticated = true
        return Mono.just(auth)
    }

    private fun authenticationDevMode(authentication: Authentication) =
        SidecarProxyAuthentication(authentication.principal.toString(), authentication.credentials.toString())

    private fun authenticationMode(authentication: Authentication) =
        JWTAuthentication(jwtValidation.validate(authentication.credentials as String))
}

@Configuration
@EnableConfigurationProperties(CreditasAuthConfigProperties::class)
@Import(
    JWTValidationService::class,
    CreditasAuthenticationProvider::class,
    CreditasAuthenticationContext::class
)
class CreditasAuthConfig
