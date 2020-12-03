package authentication

import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.AuthenticatedPrincipal
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

abstract class CreditasAuthentication(
    authorities: List<GrantedAuthority> = emptyList()
) : AbstractAuthenticationToken(authorities) {
    abstract val userId: String
    abstract val userType: String
}

private const val USER_ID_JWT_GRANT = "user_id"
private const val USER_TYPE_JWT_GRANT = "user_type"

class JWTAuthentication internal constructor(private val decodedToken: DecodedJWT) :
    CreditasAuthentication(userTypeAuthority(decodedToken)) {

    override val userId = principal.id
    override val userType = principal.type

    data class Principal(val id: String, val type: String) : AuthenticatedPrincipal {
        override fun getName() = "$type:$id"
    }

    override fun getCredentials() = decodedToken
    override fun getPrincipal() = jwtPrincipalFromToken(decodedToken)
}

fun userTypeAuthority(decodedToken: DecodedJWT): List<GrantedAuthority> {
    return userTypeAuthority(decodedToken.getClaim(USER_TYPE_JWT_GRANT)?.asString())
}

fun userTypeAuthority(userType: String?): List<GrantedAuthority> {
    val authorities = mutableListOf<GrantedAuthority>()

    if (userType != null) {
        val authority = "TYPE_${userType.toUpperCase()}"
        authorities.add(SimpleGrantedAuthority(authority))
    }

    return authorities
}

private fun jwtPrincipalFromToken(decodedToken: DecodedJWT): JWTAuthentication.Principal {
    val id = decodedToken.getClaim(USER_ID_JWT_GRANT)?.asString()
    val type = decodedToken.getClaim(USER_TYPE_JWT_GRANT)?.asString()

    if (id == null || type == null) {
        throw InvalidTokenException()
    }

    return JWTAuthentication.Principal(id = id, type = type)
}

@Suppress("ConstructorParameterNaming")
internal class SidecarProxyAuthentication(override val userId: String, override val userType: String) :
    CreditasAuthentication(userTypeAuthority(userType)) {

    data class Principal(val id: String, val Type: String)

    override fun getCredentials() = null
    override fun getPrincipal() = Principal(userId, userType)
}
