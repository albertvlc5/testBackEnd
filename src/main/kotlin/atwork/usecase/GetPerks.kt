package atwork.usecase

import atwork.gateway.IProductGateway
import atwork.valueobject.Cpf
import atwork.valueobject.Perk
import org.springframework.stereotype.Service

@Service
class GetPerks(
    private val products: List<IProductGateway>
) {
    suspend operator fun invoke(cpf: Cpf): List<Perk> = products.map { it.getPerks(cpf) }
}
