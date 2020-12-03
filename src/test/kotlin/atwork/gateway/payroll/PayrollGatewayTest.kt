package atwork.gateway.payroll

import atwork.Clock
import atwork.extension.toCpf
import atwork.extension.toJson
import atwork.helpers.buildDeductibleResponse
import atwork.helpers.buildInstallment
import atwork.helpers.buildLoan
import java.time.Month
import java.time.Year
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.asResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PayrollGatewayTest {
    private val cpf = "32475088486".toCpf()
    private val clientIdFromGateway = "62580"
    private val valueFromGateway = 988.7

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
    private lateinit var subject: PayrollGateway

    @Test
    fun `when getting perks, makes correct request`() {
        val response = LimitResponse(maximumValue = valueFromGateway)
        mockWebServer.enqueue(
            MockResponse()
                .setBody(response.toJson())
                .addHeader("Content-Type", "application/json")
        )

        runBlocking {
            subject.getPerks(cpf = cpf)
        }

        with(mockWebServer.takeRequest()) {
            assertThat(method).isEqualTo("POST")
            assertThat(path).isEqualTo("/partner/v1/payroll/simulate")
            assertThat(body.asResponseBody().string()).isEqualTo(
                mapOf("cpf" to cpf.number).toJson()
            )
            assertThat(headers["signature"]).isNotNull
            assertThat(headers["signature-stamp"]).isNotNull
            assertThat(headers["signature-key"]).isNotNull
        }
    }

    @Test
    fun `when getting total limit and got 2xx, returns correct response`() {
        val response = LimitResponse(maximumValue = valueFromGateway)
        mockWebServer.enqueue(
            MockResponse()
                .setBody(response.toJson())
                .addHeader("Content-Type", "application/json")
        )

        val returnedResponse = runBlocking {
            subject.getPerks(cpf = cpf)
        }

        assertThat(returnedResponse.totalAmountAvailable).isEqualTo(valueFromGateway)
    }

    @Test
    fun `when getting total limit and got 422, throws NonClientError`() {
        mockWebServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(422)
        )

        assertThrows<NonClientError> {
            runBlocking {
                subject.getPerks(cpf = cpf)
            }
        }
    }

    @Test
    fun `when getting total limit and got 500, throws SimulateUnknownError`() {
        mockWebServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(500)
        )

        assertThrows<SimulateUnknownError> {
            runBlocking {
                subject.getPerks(cpf = cpf)
            }
        }
    }

    @Test
    fun `when getting client id, makes correct request`() {
        val response = ClientIdResponse(id = clientIdFromGateway)
        mockWebServer.enqueue(
            MockResponse()
                .setBody(response.toJson())
                .addHeader("Content-Type", "application/json")
        )

        runBlocking {
            subject.getClientId(cpf = cpf)
        }

        with(mockWebServer.takeRequest()) {
            assertThat(method).isEqualTo("GET")
            assertThat(path).isEqualTo("/partner/v1/persons/$cpf")
            assertThat(headers["signature"]).isNotNull
            assertThat(headers["signature-stamp"]).isNotNull
            assertThat(headers["signature-key"]).isNotNull
        }
    }

    @Test
    fun `when getting client id and got 2xx, returns correct response`() {
        val response = ClientIdResponse(id = clientIdFromGateway)
        mockWebServer.enqueue(
            MockResponse()
                .setBody(response.toJson())
                .addHeader("Content-Type", "application/json")
        )

        val returnedResponse = runBlocking {
            subject.getClientId(cpf = cpf)
        }

        assertThat(returnedResponse).isEqualTo(clientIdFromGateway)
    }

    @Test
    fun `when getting client id and got 422, throws NonClientError`() {
        mockWebServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(422)
        )

        assertThrows<NonClientError> {
            runBlocking {
                subject.getClientId(cpf = cpf)
            }
        }
    }

    @Test
    fun `when getDeductibles and client does not exist or does not have deductibles, returns emptyList`() {
        mockWebServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(204)
        )
        runBlocking {
            val response = subject.getDeductibles(cpf = cpf, month = Month.APRIL, year = Year.of(2020))
            assertThat(response).isEmpty()
        }
    }

    @Test
    fun `when getDeductibles and client has deductibles, returns deductibles`() {
        val date = Clock.getCurrentLocalDate()
        val deductiblesResponse = listOf(
            buildDeductibleResponse(
                installment = buildInstallment(
                    dueDate = date.plusMonths(1),
                    loan = buildLoan(requestDate = date.minusMonths(1), type = LoanTypeResponse.BALLOON)
                )
            ),
            buildDeductibleResponse(
                installment = buildInstallment(
                    number = 2,
                    dueDate = date.plusMonths(1),
                    loan = buildLoan(requestDate = date.minusMonths(3), type = LoanTypeResponse.REQUESTING)
                )
            ),
            buildDeductibleResponse(
                installment = buildInstallment(
                    number = 1,
                    dueDate = date.plusMonths(1),
                    loan = buildLoan(
                        requestDate = date.minusMonths(1),
                        type = LoanTypeResponse.SALARY_ADVANCE,
                        totalInstallments = 1
                    )
                )
            )
        )

        val expectedResponse = deductiblesResponse.map { it.toDeductible() }

        mockWebServer.enqueue(
            MockResponse()
                .setBody(deductiblesResponse.toJson())
                .addHeader("Content-Type", "application/json")
        )

        runBlocking {
            val deductibles = subject.getDeductibles(
                cpf = cpf,
                month = date.month,
                year = Year.of(date.year)
            )
            assertThat(deductibles).isNotEmpty
            assertThat(deductibles).isEqualTo(expectedResponse)
        }
    }
}
