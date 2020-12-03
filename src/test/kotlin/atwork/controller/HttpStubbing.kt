package atwork.controller

import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.jupiter.api.fail
import org.springframework.http.HttpMethod

fun stub(httpStubs: List<HttpStub>) = httpStubs.forEach {
    WireMock.stubFor(
        when (it.httpMethod) {
            HttpMethod.GET -> WireMock.get(WireMock.urlEqualTo(it.path))
            HttpMethod.POST -> WireMock.post(WireMock.urlEqualTo(it.path))
            else -> throw fail { "Non stubbed request for ${it.httpMethod} HTTP method was made." }
        }.willReturn(
            WireMock.aResponse()
                .withStatus(it.responseCode)
                .withHeader("Content-Type", "application/json")
                .withBody(it.responseBody)
        )
    )
}
