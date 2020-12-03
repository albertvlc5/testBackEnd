package authentication

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("creditas-auth")
class CreditasAuthConfigProperties {
    /**
     * Enable Creditas Auth integration
     */
    var enabled: Boolean = true

    /**
     * Enable Dev mode authentication
     */
    var devModeEnabled: Boolean = false

    val gateway = GatewayConfigurationProperties()
}

class GatewayConfigurationProperties {
    /**
     * The API Gateway public key used for signing JWTs, in PEM format
     */
    lateinit var publicKeyPem: String
}
