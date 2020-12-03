package atwork.extension

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun Map<String, Any>.toJson() = jacksonObjectMapper().writeValueAsString(this)
