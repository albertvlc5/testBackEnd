package atwork

import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
object Clock {
    private var diff = 0L

    fun getCurrentDateAsString() = System.currentTimeMillis().toString()
    fun getCurrentLocalDateTime(): LocalDateTime = LocalDateTime.now().minusSeconds(diff)
    fun getCurrentLocalDate(): LocalDate = getCurrentLocalDateTime().toLocalDate()
}
