package atwork.usecase

import atwork.Clock
import atwork.controller.responses.Deductible
import atwork.gateway.IDeductibleGateway
import atwork.valueobject.Cpf
import java.time.Year
import org.springframework.stereotype.Service

@Service
class GetDeductibles(
    private val deductibleGateway: IDeductibleGateway,
) {
    companion object {
        private const val DAY_CHANGE_PERIOD = 15
    }

    suspend operator fun invoke(cpf: Cpf): List<Deductible> {
        val currentDate = Clock.getCurrentLocalDate()
        val dateForDeductibles = when {
            currentDate.dayOfMonth < DAY_CHANGE_PERIOD -> currentDate
            else -> currentDate.plusMonths(1)
        }

        return deductibleGateway.getDeductibles(cpf, dateForDeductibles.month, Year.of(dateForDeductibles.year))
    }
}
