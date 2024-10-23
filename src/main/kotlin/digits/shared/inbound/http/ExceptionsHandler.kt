package digits.shared.inbound.http

import digits.shared.inbound.http.errors.ApiError
import digits.shared.inbound.http.errors.ApiErrors
import digits.shared.BaseException
import digits.shared.inbound.http.errors.ApiErrorConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.util.UUID.randomUUID

@RestControllerAdvice
class ExceptionsHandler(private val apiErrorConverter: ApiErrorConverter) {

    private val log = LoggerFactory.getLogger(ExceptionsHandler::class.java)

    @ExceptionHandler(BaseException::class)
    fun handle(ex: BaseException, request: WebRequest): ResponseEntity<ApiErrors> {
        log.info(ex.message, ex)
        val status = apiErrorConverter.convert(ex)
        val error = ApiError(randomUUID().toString(), status.toString(), "Client error", ex.message)
        return ResponseEntity(ApiErrors(listOf(error)), status)
    }

    @ExceptionHandler(HttpMessageConversionException::class, BindException::class, TypeMismatchException::class)
    fun spring(ex: Exception, request: WebRequest): ResponseEntity<ApiErrors> {
        log.info(ex.message, ex)
        val error = ApiError(randomUUID().toString(), BAD_REQUEST.toString(), "Client error", ex.message)
        return ResponseEntity(ApiErrors(listOf(error)), BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception, request: WebRequest): ResponseEntity<ApiErrors> {
        log.error(ex.message, ex)
        val uuid = randomUUID().toString()
        val error = ApiError(uuid, INTERNAL_SERVER_ERROR.toString(), "Server error", "")
        return ResponseEntity(ApiErrors(listOf(error)), INTERNAL_SERVER_ERROR)
    }
}
