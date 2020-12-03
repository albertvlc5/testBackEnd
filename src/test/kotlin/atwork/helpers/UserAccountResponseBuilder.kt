package atwork.helpers

import atwork.gateway.authentication.Customer
import atwork.gateway.authentication.UserAccountResponse
import java.util.UUID

fun buildUserAccountResponse(
    id: UUID = UUID.randomUUID(),
    screenName: String = "Test User",
    customerId: UUID = UUID.randomUUID(),
    authenticationClaims: List<Map<String, String>> = listOf(
        mapOf(
            "id" to "eed34b38-3677-41f9-86b2-eaf31417a363",
            "type" to "EMAIL",
            "emailAddress" to "test@mail.com",
            "status" to "VERIFIED"

        ),
        mapOf(
            "id" to "298ff648-f0f5-4bd3-b003-6c7e71511db7",
            "type" to "MAIN_DOCUMENT",
            "documentCode" to "31795002042",
            "documentType" to "CPF",
            "status" to "VERIFIED"
        )
    ),
) = UserAccountResponse(
    id = id.toString(),
    screenName = screenName,
    customer = Customer(customerId.toString()),
    authenticationClaims = authenticationClaims
)
