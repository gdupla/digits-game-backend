package digits.shared.inbound.http.errors

import digits.shared.AccessDenied
import digits.shared.InvalidArgument
import digits.shared.InvalidState
import digits.shared.NotFound
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.http.HttpStatus

class ApiErrorConverterTest {
    private val apiErrorConverter = ApiErrorConverter()

    @TestFactory
    fun `Given an exception When convert Then it will be correctly map`() = listOf(
        Pair(InvalidArgument(message = "InvalidArgument"), HttpStatus.BAD_REQUEST),
        Pair(InvalidState(message = "InvalidState"), HttpStatus.BAD_REQUEST),
        Pair(AccessDenied(message = "AccessDenied"), HttpStatus.FORBIDDEN),
        Pair(NotFound(message = "NotFound"), HttpStatus.NOT_FOUND),
    ).map {
        DynamicTest.dynamicTest("should convert ${it.first.message}") {
            val actualHttpStatus = apiErrorConverter.convert(it.first)
            assertThat(actualHttpStatus).isEqualTo(it.second)
        }
    }
}