package atwork.gateway

import atwork.valueobject.Cpf
import atwork.valueobject.Perk

interface IProductGateway {
    suspend fun getPerks(cpf: Cpf): Perk
}
