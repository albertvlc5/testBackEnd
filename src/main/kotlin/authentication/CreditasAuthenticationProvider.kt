package authentication

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["creditas-auth.enabled"], matchIfMissing = true)
class CreditasAuthenticationProvider : AuthenticationProvider {
    override fun authenticate(auth: Authentication?): CreditasAuthentication? {
        if (auth !is CreditasAuthentication) {
            return null
        }

        auth.isAuthenticated = true
        return auth
    }

    override fun supports(authentication: Class<*>?): Boolean {
        if (authentication == null) {
            return false
        }

        val authBaseClass = CreditasAuthentication::class.java
        return authBaseClass.isAssignableFrom(authentication)
    }
}
