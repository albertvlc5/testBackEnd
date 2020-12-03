package atwork.shared

import atwork.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

fun Clock.setNow(date: LocalDateTime) {
    javaClass.getDeclaredField("diff").let {
        val diff = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - date.toEpochSecond(ZoneOffset.UTC)
        it.isAccessible = true
        it.setLong(Clock, diff)
    }
}

fun Clock.reset() {
    javaClass.getDeclaredField("diff").let {
        it.isAccessible = true
        it.setLong(Clock, 0)
    }
}
