package atwork.extension

import atwork.valueobject.Cpf
import java.security.MessageDigest

fun String.toMD5(): String {
    val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return bytes.toHex()
}

fun String.toCpf(): Cpf = Cpf(this)
