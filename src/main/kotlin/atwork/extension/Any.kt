package atwork.extension

import atwork.configuration.Jackson.Companion.mapper

fun Any.toJson(): String = mapper.writeValueAsString(this)
