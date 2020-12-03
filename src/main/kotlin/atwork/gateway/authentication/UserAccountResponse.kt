package atwork.gateway.authentication

import atwork.valueobject.Cpf
import atwork.valueobject.UserInfo
import java.util.UUID

data class UserAccountResponse(
    val id: String,
    val screenName: String?,
    val customer: Customer?,
    val authenticationClaims: List<Map<String, String>>
) {
    companion object {
        private const val CLAIM_TYPE = "type"
        private const val CLAIM_TYPE_EMAIL = "EMAIL"
        private const val CLAIM_TYPE_MAIN_DOCUMENT = "MAIN_DOCUMENT"
        private const val CLAIM_STATUS = "status"
        private const val CLAIM_STATUS_VERIFIED = "VERIFIED"
        private const val CLAIM_EMAIL_ADDRESS = "emailAddress"
        private const val CLAIM_DOCUMENT_CODE = "documentCode"
    }

    fun toUserInfo(): UserInfo =
        UserInfo(
            id = UUID.fromString(id),
            name = screenName,
            customerId = if (customer?.id != null) UUID.fromString(customer.id) else null,
            email = authenticationClaims.firstOrNull {
                it[CLAIM_TYPE] == CLAIM_TYPE_EMAIL && it[CLAIM_STATUS] == CLAIM_STATUS_VERIFIED
            }?.get(CLAIM_EMAIL_ADDRESS),
            cpf = Cpf(
                authenticationClaims.first { it[CLAIM_TYPE] == CLAIM_TYPE_MAIN_DOCUMENT }
                    .getValue(CLAIM_DOCUMENT_CODE)
            )
        )
}

data class Customer(val id: String)
