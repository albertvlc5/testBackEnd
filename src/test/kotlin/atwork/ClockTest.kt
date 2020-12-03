package atwork

import atwork.shared.setNow
import java.time.LocalDate
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class ClockTest {

    @AfterEach
    fun resetClock() = Clock.setNow(LocalDateTime.now())

    @Test
    fun `getCurrentLocalDateTime, return the current date and time`() {
        val expected = LocalDateTime.now()
        val result = Clock.getCurrentLocalDateTime()

        assertThat(result).isEqualToIgnoringNanos(expected)
    }

    @Test
    fun `getCurrentLocalDate, return the current date`() {
        val expected = LocalDate.now()
        val result = Clock.getCurrentLocalDate()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `when set new date time for clock, getCurrentLocalDateTime return that date and time`() {
        val dateToSet = LocalDateTime.of(2019, 10, 5, 21, 27, 35)
        Clock.setNow(dateToSet)
        val result = Clock.getCurrentLocalDateTime()

        assertThat(result).isEqualToIgnoringNanos(dateToSet)
    }

    @Test
    fun `when set new date for clock, getCurrentLocalDate return that date`() {
        val dateToSet = LocalDateTime.of(2018, 5, 31, 12, 26, 43)
        Clock.setNow(dateToSet)
        val result = Clock.getCurrentLocalDate()

        assertThat(result).isEqualTo(dateToSet.toLocalDate())
    }
}
