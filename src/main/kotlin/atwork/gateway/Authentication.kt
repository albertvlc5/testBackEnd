package atwork.gateway

import atwork.Clock
import atwork.extension.toMD5
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

sealed class Authentication {

    abstract fun getHeaders(): Map<String, String>

    @Service
    class Payroll(
        @Value("\${payroll.key.public}") private val publicKey: String,
        @Value("\${payroll.key.private}") private val privateKey: String,
        private val clock: Clock
    ) : Authentication() {

        companion object {
            private const val SIGNATURE_KEY_HEADER = "signature-key"
            private const val SIGNATURE_STAMP_HEADER = "signature-stamp"
            private const val SIGNATURE_HEADER = "signature"
        }

        private fun signatureStamp() = clock.getCurrentDateAsString()

        override fun getHeaders(): Map<String, String> {
            val signatureStamp = signatureStamp()

            return mapOf(
                SIGNATURE_KEY_HEADER to publicKey,
                SIGNATURE_STAMP_HEADER to signatureStamp,
                SIGNATURE_HEADER to "$privateKey$signatureStamp".toMD5()
            )
        }
    }
}
