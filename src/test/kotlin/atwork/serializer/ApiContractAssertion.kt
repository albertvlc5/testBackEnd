package atwork.serializer

import java.io.InputStreamReader
import org.assertj.core.api.AbstractAssert
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.core.io.ClassPathResource

class ApiContractAssertion(private val response: String) :
    AbstractAssert<ApiContractAssertion, Any>(response, ApiContractAssertion::class.java) {
    fun matchesJsonFile(
        resourcePath: String,
        compareMode: JSONCompareMode = JSONCompareMode.LENIENT
    ): ApiContractAssertion {
        val expectedResponse = getStubContent(resourcePath)

        JSONCompare.compareJSON(
            expectedResponse,
            response,
            compareMode
        ).apply {
            if (failed()) {
                failWithMessage("Contracts don't match. $message")
            }
        }

        return this
    }

    companion object {
        fun assertThatResponse(response: String) = ApiContractAssertion(response)
        fun getStubContent(resourcePath: String): String =
            InputStreamReader(ClassPathResource(resourcePath).inputStream)
                .readLines()
                .joinToString(separator = "") {
                    it.trim().replace(": ", ":")
                }
    }
}
