package atwork.extension

fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
