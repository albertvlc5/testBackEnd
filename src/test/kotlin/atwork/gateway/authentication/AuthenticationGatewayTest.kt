package atwork.gateway.authentication

import atwork.extension.toJson
import atwork.gateway.UnauthorizedException
import atwork.valueobject.Cpf
import atwork.controller.JWTToken
import atwork.valueobject.UserInfo
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest
class AuthenticationGatewayTest {

    @Autowired
    private lateinit var subject: AuthenticationGateway
    private lateinit var mockWebServer: MockWebServer

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(port = 8089)
    }

    @AfterEach
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `When a valid token is received get an UserInfo`() {
        val token = JWTToken("xasdsd.asdadaderwer.asd")
        val emailAddress = "test@mail.com"
        val cpf = "39239567038"
        val response = UserAccountResponse(
            id = UUID.randomUUID().toString(),
            screenName = "Test User",
            customer = Customer(UUID.randomUUID().toString()),
            authenticationClaims = listOf(
                mapOf(
                    "id" to UUID.randomUUID().toString(),
                    "type" to "EMAIL",
                    "emailAddress" to emailAddress,
                    "status" to "VERIFIED"
                ),
                mapOf(
                    "id" to "298ff648-f0f5-4bd3-b003-6c7e71511db7",
                    "type" to "MAIN_DOCUMENT",
                    "documentCode" to cpf,
                    "documentType" to "CPF",
                    "status" to "VERIFIED"
                )
            )
        )
        val userInfo = UserInfo(
            id = UUID.fromString(response.id),
            name = response.screenName,
            customerId = UUID.fromString(response.customer!!.id),
            email = emailAddress,
            cpf = Cpf(cpf)
        )

        mockWebServer.enqueue(
            MockResponse()
                .setBody(response.toJson())
                .addHeader("Content-Type", "application/json")
        )

        runBlocking {
            val userInfoResponse = subject.getUserInfo(token)
            assertThat(userInfoResponse).isEqualTo(userInfo)
        }

        with(mockWebServer.takeRequest()) {
            assertThat(method).isEqualTo("GET")
            assertThat(path).isEqualTo("/auth/accounts/current?includes=authenticationClaims,customer")
            assertThat(headers["accept"]).isEqualTo("application/vnd.creditas.v1+json")
            assertThat(headers["authorization"]).isEqualTo("Bearer $token")
        }
    }

    @Test
    fun `When an invalid token is received, an UnauthorizedException is thrown`() {
        val invalidToken = JWTToken("123.dfdasf23.123dsasd")
        mockWebServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(401)
        )

        assertThrows<UnauthorizedException> {
            runBlocking {
                subject.getUserInfo(invalidToken)
            }
        }
    }
}
