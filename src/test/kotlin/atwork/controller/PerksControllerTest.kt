package atwork.controller

import atwork.shared.ApiContractAssertion.Companion.assertThatResponse
import authentication.test.WithMockCreditasAuth
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureWebTestClient
class PerksControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

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
    @WithMockCreditasAuth
    fun `when calling Perks endpoint returns Perks list`() {
        mockWebServer.dispatcher = RequestDispatcher()

        val response = webTestClient.get()
            .uri("/perks")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 123.456.789")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertThatResponse(response!!).matchesJsonFile("perks/perks_response.json")
    }

    @Test
    fun `when calling Perks without Authorization header, returns 401`() {
        webTestClient.get()
            .uri("/perks")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    @WithMockCreditasAuth
    fun `when calling Perks with invalid Authorization header, returns 401`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
        )

        webTestClient.get()
            .uri("/perks")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer 123.456.789")
            .exchange()
            .expectStatus().isUnauthorized
    }
}
