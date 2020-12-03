package atwork.gateway

import atwork.controller.responses.Deductible
import atwork.valueobject.Cpf
import java.time.Month
import java.time.Year

interface IDeductibleGateway {
    suspend fun getDeductibles(cpf: Cpf, month: Month, year: Year): List<Deductible>
}
