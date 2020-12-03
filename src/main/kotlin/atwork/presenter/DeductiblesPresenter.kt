package atwork.presenter

import atwork.controller.responses.Deductible
import atwork.usecase.GetDeductibles
import atwork.valueobject.Cpf
import org.springframework.stereotype.Service

@Service
class DeductiblesPresenter(
    private val getDeductibles: GetDeductibles
) {
    suspend fun getAllDeductibles(cpf: Cpf): List<Deductible> = getDeductibles(cpf)
}
