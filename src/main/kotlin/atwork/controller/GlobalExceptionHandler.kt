package atwork.controller

import atwork.gateway.UnauthorizedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(value = [UnauthorizedException::class])
    fun handleUnauthorizedException(exception: Throwable): ResponseEntity<HttpStatus> =
        status(HttpStatus.UNAUTHORIZED).build()
}
