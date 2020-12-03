package atwork.valueobject

import atwork.extension.toMoney
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round

data class Money(
    val amount: Double
) {
    companion object {
        const val MONEY_CONVERSION_RATE = 100.0
        private val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    val cents: Int
        get() = round(amount * MONEY_CONVERSION_RATE).toInt()

    override fun toString(): String = formatter.format(amount)

    operator fun plus(other: Money) = (amount + other.amount).toMoney()

    operator fun minus(other: Money) = (amount - other.amount).toMoney()

    fun toDouble() = amount
}
