package atwork.gateway.salaryadvance

import atwork.extension.toCpf
import atwork.extension.toJson
import atwork.gateway.payroll.ClientIdResponse
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SalaryAdvanceGatewayTest {
    private val cpf = "32475088486".toCpf()
    private val clientIdFromGateway = "62580"
    private val amountAvailableToAdvanceToday = 99.15
    private val amountAdvancedInCurrentCycle = 100.99
    private val clientIdResponse = ClientIdResponse(id = clientIdFromGateway)
    private val salaryAdvanceResponse = SummaryResponse(
        amountAvailableToAdvanceToday = amountAvailableToAdvanceToday,
        amountAdvancedInCurrentCycle = amountAdvancedInCurrentCycle
    )

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

    @Autowired
    private lateinit var subject: SalaryAdvanceGateway

    @Test
    fun `when getting total limit makes correct request`() {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(clientIdResponse.toJson())
                .addHeader("Content-Type", "application/json")
        )
        mockWebServer.enqueue(
            MockResponse()
                .setBody(salaryAdvanceResponse.toJson())
                .addHeader("Content-Type", "application/json")
        )

        runBlocking {
            subject.getPerks(cpf = cpf)
        }

        mockWebServer.takeRequest()
        with(mockWebServer.takeRequest()) {
            assertThat(method).isEqualTo("GET")
            assertThat(path).isEqualTo("/salary-advance/client/$clientIdFromGateway/summary")
        }
    }

    @Test
    fun `when getting limit and 2xx, returns correct response`() {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(clientIdResponse.toJson())
                .addHeader("Content-Type", "application/json")
        )
        mockWebServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(salaryAdvanceResponse.toJson())
        )

        val returnedResponse = runBlocking {
            subject.getPerks(cpf = cpf)
        }

        assertThat(returnedResponse).isEqualTo(salaryAdvanceResponse.toDomain())
    }
}
