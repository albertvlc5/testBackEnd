package atwork.configuration

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.text.SimpleDateFormat
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class Jackson {
    @Bean
    fun jackson2ObjectMapperBuilder() = objectMapperBuilder

    @Bean
    fun webFluxConfigurer(): WebFluxConfigurer {
        return object : WebFluxConfigurer {
            override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
                configurer.defaultCodecs().jackson2JsonDecoder(
                    Jackson2JsonDecoder(jackson2ObjectMapperBuilder().build())
                )
                configurer.defaultCodecs().jackson2JsonEncoder(
                    Jackson2JsonEncoder(jackson2ObjectMapperBuilder().build())
                )
            }
        }
    }

    companion object {
        private val objectMapperBuilder: Jackson2ObjectMapperBuilder by lazy {
            Jackson2ObjectMapperBuilder()
                .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .dateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"))
        }

        val mapper: ObjectMapper = objectMapperBuilder.build()
    }
}
