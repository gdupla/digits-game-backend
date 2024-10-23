package digits.shared.inbound.http.errors

import digits.shared.AccessDenied
import digits.shared.BaseException
import digits.shared.InvalidArgument
import digits.shared.InvalidState
import digits.shared.NotFound
import digits.shared.Unauthorized
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class ApiErrorConverter : Converter<BaseException, HttpStatus> {
    override fun convert(source: BaseException): HttpStatus = getStatusCode(source)

    private fun getStatusCode(source: BaseException) = when (source) {
        is InvalidArgument -> HttpStatus.BAD_REQUEST
        is InvalidState -> HttpStatus.BAD_REQUEST
        is AccessDenied -> HttpStatus.FORBIDDEN
        is NotFound -> HttpStatus.NOT_FOUND
        is Unauthorized -> HttpStatus.UNAUTHORIZED
    }
}
