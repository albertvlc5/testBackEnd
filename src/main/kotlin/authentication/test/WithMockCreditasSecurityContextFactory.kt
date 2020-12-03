package authentication.test

import authentication.CreditasAuthentication
import org.springframework.security.core.AuthenticatedPrincipal
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithMockCreditasSecurityContextFactory :
    WithSecurityContextFactory<WithMockCreditasAuth> {
    override fun createSecurityContext(annotation: WithMockCreditasAuth): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()

        context.authentication = CreditasTestSupportAuthentication(
            annotation.userId,
            annotation.userType,
            userTypeAuthority(annotation.userType)
        )

        return context
    }

    private fun userTypeAuthority(userType: String): List<GrantedAuthority> =
        mutableListOf<GrantedAuthority>(SimpleGrantedAuthority("TYPE_${userType.toUpperCase()}"))
}

internal class CreditasTestSupportAuthentication(
    override val userId: String,
    override val userType: String,
    authorities: List<GrantedAuthority>
) : CreditasAuthentication(authorities) {
    override fun getCredentials() = "$userType:$userId"
    override fun getPrincipal() = AuthenticatedPrincipal { "$userType:$userId" }
    override fun isAuthenticated() = true
}
