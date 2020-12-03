package atwork.usecase

import atwork.controller.responses.LoanType
import atwork.extension.toCpf
import atwork.gateway.IProductGateway
import atwork.helpers.buildPerk
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetPerksTest {
    private val firstProductGateway = mockk<IProductGateway>()
    private val secondProductGateway = mockk<IProductGateway>()
    private val cpf = "123456".toCpf()

    private val getPerks = GetPerks(listOf(firstProductGateway, secondProductGateway))

    @Test
    fun `collects all perks from gateways`() {
        val firstPerk = buildPerk(type = LoanType.SALARY_ADVANCEMENT)
        val secondPerk = buildPerk(type = LoanType.PAYROLL_STORE)
        every { runBlocking { firstProductGateway.getPerks(cpf) } } returns firstPerk
        every { runBlocking { secondProductGateway.getPerks(cpf) } } returns secondPerk

        val allPerks = runBlocking { getPerks(cpf) }

        assertThat(allPerks).containsExactlyInAnyOrder(
            firstPerk,
            secondPerk
        )
    }
}
