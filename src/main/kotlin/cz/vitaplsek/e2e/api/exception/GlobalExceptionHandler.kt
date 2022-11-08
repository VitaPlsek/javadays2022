package cz.vitaplsek.e2e.api.exception

import org.jooq.exception.NoDataFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoDataFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun handleNoDataFound(ex: NoDataFoundException) =
        ex.message
}
