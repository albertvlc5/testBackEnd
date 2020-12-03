package atwork.presenter

import atwork.usecase.GetPerks
import atwork.valueobject.Cpf
import atwork.valueobject.Perk
import org.springframework.stereotype.Service

@Service
class PerkPresenter(
    private val getPerks: GetPerks
) {
    suspend fun getAllPerks(cpf: Cpf): List<Perk> = getPerks(cpf)
}
