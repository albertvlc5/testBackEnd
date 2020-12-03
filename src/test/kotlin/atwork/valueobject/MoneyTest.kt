package atwork.valueobject

import java.text.NumberFormat
import java.util.Locale
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MoneyTest {

    private val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    @Test
    fun `when money has exact cents, String representation formats it correctly`() {
        assertThat(Money(122.45).toString()).isEqualTo(formatter.format(122.45))
    }

    @Test
    fun `when money has not exact cents, String representation rounds it`() {
        assertThat(Money(122.456).toString()).isEqualTo(formatter.format(122.46))
    }

    @Test
    fun `when money has not cents, String representation formats it with cents`() {
        assertThat(Money(122.0).toString()).isEqualTo(formatter.format(122.00))
    }

    @Test
    fun `when money has decimal cents, String representation formats it with cents`() {
        assertThat(Money(122.1).toString()).isEqualTo(formatter.format(122.10))
    }

    @Test
    fun `when money is greater than a thousand, String representation separates it with dots`() {
        assertThat(Money(1122.1).toString()).isEqualTo(formatter.format(1122.10))
    }

    @Test
    fun `cents contains the amount of money in cents`() {
        assertThat(Money(122.45).cents).isEqualTo(12245)
    }

    @Test
    fun `cents rounds to the ceil when the amount has high decimals`() {
        assertThat(Money(122.456).cents).isEqualTo(12246)
    }

    @Test
    fun `cents rounds to the ground when the amount has low decimals`() {
        assertThat(Money(122.453).cents).isEqualTo(12245)
    }
}
