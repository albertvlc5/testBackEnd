package authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service

class InvalidTokenException(cause: Throwable? = null) : AuthenticationException("Invalid Authentication Token", cause)

@Service
class JWTValidationService(
    config: CreditasAuthConfigProperties
) {
    private val publicKeyPem = config.gateway.publicKeyPem

    fun validate(token: String): DecodedJWT {
        try {
            return jwtVerifier.verify(token)
        } catch (e: JWTVerificationException) {
            throw InvalidTokenException(e)
        }
    }

    val jwtVerifier: JWTVerifier by lazy {
        val key = publicEcKeyFromPem(publicKeyPem)
        val algorithm = Algorithm.ECDSA256(key, null)
        JWT.require(algorithm).build()
    }
}
