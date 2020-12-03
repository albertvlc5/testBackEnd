package atwork.configuration

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.metrics.AutoTimer
import org.springframework.boot.actuate.metrics.web.reactive.client.DefaultWebClientExchangeTagsProvider
import org.springframework.boot.actuate.metrics.web.reactive.client.MetricsWebClientFilterFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClient {

    companion object {
        private const val MAX_MEMORY_512_KB: Int = 512 * 1024
    }

    @Bean
    fun httpClient(builder: WebClient.Builder, meterRegister: MeterRegistry): WebClient {
        val metricsFilter = MetricsWebClientFilterFunction(
            meterRegister,
            DefaultWebClientExchangeTagsProvider(),
            "webClientMetrics",
            AutoTimer.ENABLED
        )

        return builder
            .exchangeStrategies(exchangeStrategies())
            .filter(metricsFilter)
            .build()
    }

    private fun exchangeStrategies() = ExchangeStrategies.builder()
        .codecs { it.defaultCodecs().maxInMemorySize(MAX_MEMORY_512_KB) }
        .build()
}
