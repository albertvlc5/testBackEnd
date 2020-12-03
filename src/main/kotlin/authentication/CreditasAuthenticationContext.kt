package authentication

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class CreditasAuthenticationContext {
    val current: CreditasAuthentication?
        get() = SecurityContextHolder.getContext().authentication as? CreditasAuthentication
}
