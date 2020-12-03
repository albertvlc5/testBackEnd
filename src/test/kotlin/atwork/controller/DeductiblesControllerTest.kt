package atwork.controller

import atwork.Clock
import atwork.extension.toJson
import atwork.gateway.payroll.LoanTypeResponse
import atwork.helpers.buildDeductibleResponse
import atwork.helpers.buildInstallment
import atwork.helpers.buildLoan
import atwork.helpers.buildUserAccountResponse
import atwork.shared.ApiContractAssertion.Companion.assertThatResponse
import atwork.shared.setNow
import authentication.test.WithMockCreditasAuth
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureWebTestClient(timeout = "60000")
@AutoConfigureWireMock(port = 8089)
class DeductiblesControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @AfterEach
    fun resetClock() = Clock.setNow(LocalDateTime.now())

    @Test
    @WithMockCreditasAuth
    fun `when calling Deductibles endpoint returns Deductibles list`() {
        stubHttpRequests()

        val response = webTestClient.get()
            .uri("/deductibles")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 123.456.7890")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertThatResponse(response!!).matchesJsonFile("deductibles/deductibles_response.json")
    }

    @Test
    @WithMockCreditasAuth
    fun `when calling Deductibles endpoint and no deductibles are available, return empty list`() {
        stubHttpRequestForNoDeductibles()

        val response = webTestClient.get()
            .uri("/deductibles")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 123.456.7890")
            .exchange()
            .expectStatus().isOk
            .expectBody(Array<String>::class.java)
            .returnResult()
            .responseBody

        assertThat(response!!).isEmpty()
    }

    @Test
    @WithMockCreditasAuth
    fun `when calling V2 Deductibles endpoint returns Deductibles list`() {
        stubHttpRequests()

        val response = webTestClient.get()
            .uri("/v2/deductibles")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 123.456.7890")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertThatResponse(response!!).matchesJsonFile("deductibles/deductibles_response_V2.json")
    }

    @Test
    @WithMockCreditasAuth
    fun `when calling V2 Deductibles endpoint and no deductibles are available, return empty list`() {
        stubHttpRequestForNoDeductibles()

        val response = webTestClient.get()
            .uri("/v2/deductibles")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 123.456.7890")
            .exchange()
            .expectStatus().isOk
            .expectBody(Array<String>::class.java)
            .returnResult()
            .responseBody

        assertThat(response!!).isEmpty()
    }

    private fun stubHttpRequests() {
        val dateToSet = LocalDateTime.of(2020, 11, 13, 21, 27, 35)
        val accountResponse = buildUserAccountResponse()
        val cpf = accountResponse.authenticationClaims.first { it.containsKey("documentCode") }.getValue("documentCode")
        val deductibleResponse = buildDeductibleResponse(
            installment = buildInstallment(
                amount = 100.0,
                dueDate = LocalDate.of(2020, 12, 4),
                loan = buildLoan(type = LoanTypeResponse.SALARY_ADVANCE, amount = 100.0)
            )
        )
        Clock.setNow(dateToSet)
        stub(
            listOf(
                UserAccountHttpStub(responseBody = accountResponse.toJson()),
                AangDeductiblesHttpStub(
                    cpf = cpf,
                    month = dateToSet.month,
                    Year.of(dateToSet.year),
                    responseBody = listOf(deductibleResponse).toJson()
                )
            )
        )
    }

    private fun stubHttpRequestForNoDeductibles() {
        val dateToSet = LocalDateTime.of(2020, 11, 13, 21, 27, 35)
        val accountResponse = buildUserAccountResponse()
        val cpf = accountResponse.authenticationClaims.first { it.containsKey("documentCode") }.getValue("documentCode")

        Clock.setNow(dateToSet)
        stub(
            listOf(
                UserAccountHttpStub(responseBody = accountResponse.toJson()),
                AangDeductiblesHttpStub(
                    cpf = cpf,
                    month = dateToSet.month,
                    Year.of(dateToSet.year),
                    responseCode = HttpStatus.NO_CONTENT.value()
                )
            )
        )
    }
}
