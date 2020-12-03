package atwork.controller.responses

import java.time.LocalDate

sealed class Deductible(
    val type: LoanType,
    val amountDeductibleThisMonth: Double,
    val symbol: String,
    val dueDate: LocalDate
) {

    class SalaryAdvancement(type: LoanType, amountDeductibleThisMonth: Double, symbol: String, dueDate: LocalDate) :
        Deductible(type, amountDeductibleThisMonth, symbol, dueDate) {

        override fun toString(): String {
            return "SalaryAdvancement(type=$type, amountDeductibleThisMonth=$amountDeductibleThisMonth, " +
                "symbol='$symbol', dueDate=$dueDate)"
        }
    }

    @Suppress("LongParameterList")
    class Payroll(
        type: LoanType,
        amountDeductibleThisMonth: Double,
        symbol: String,
        dueDate: LocalDate,
        val totalAmountDeducted: Double?,
        val totalAmountRequested: Double,
        val totalInstallments: Int,
        val installmentsDeducted: Int
    ) : Deductible(type, amountDeductibleThisMonth, symbol, dueDate) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Payroll

            if (totalAmountDeducted != other.totalAmountDeducted) return false
            if (totalAmountRequested != other.totalAmountRequested) return false
            if (totalInstallments != other.totalInstallments) return false
            if (installmentsDeducted != other.installmentsDeducted) return false

            return true
        }

        override fun hashCode(): Int {
            var result = totalAmountDeducted.hashCode()
            result = 31 * result + totalAmountRequested.hashCode()
            result = 31 * result + totalInstallments
            result = 31 * result + installmentsDeducted
            return result
        }

        override fun toString(): String {
            return "Payroll(type=$type, amountDeductibleThisMonth=$amountDeductibleThisMonth, symbol='$symbol', " +
                "dueDate=$dueDate, totalAmountDeducted=$totalAmountDeducted, " +
                "totalAmountRequested=$totalAmountRequested, totalInstallments=$totalInstallments, " +
                "installmentsDeducted=$installmentsDeducted)"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Deductible

        if (type != other.type) return false
        if (amountDeductibleThisMonth != other.amountDeductibleThisMonth) return false
        if (symbol != other.symbol) return false
        if (dueDate != other.dueDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + amountDeductibleThisMonth.hashCode()
        result = 31 * result + symbol.hashCode()
        result = 31 * result + dueDate.hashCode()
        return result
    }
}
