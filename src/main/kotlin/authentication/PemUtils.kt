package authentication

import org.bouncycastle.util.io.pem.PemReader
import java.io.StringReader
import java.security.KeyFactory
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

/* Adapted from https://gist.github.com/lbalmaceda/9a0c7890c2965826c04119dcfb1a5469 */

private val keyFactoryEc = KeyFactory.getInstance("EC")

internal fun publicEcKeyFromPem(content: String): ECPublicKey {
    val bytes = pemToBytes(content)
    val key = keyFactoryEc.generatePublic(X509EncodedKeySpec(bytes))

    return key as ECPublicKey
}

internal fun privateEcKeyFromPem(content: String): ECPrivateKey {
    val bytes = pemToBytes(content)
    val key = keyFactoryEc.generatePrivate(PKCS8EncodedKeySpec(bytes))

    return key as ECPrivateKey
}

private fun pemToBytes(content: String): ByteArray {
    val withNewlines = content.replace("\\n", "\n")

    return StringReader(withNewlines).use {
        val reader = PemReader(it)
        val pemObject = reader.readPemObject()

        pemObject.content
    }
}
