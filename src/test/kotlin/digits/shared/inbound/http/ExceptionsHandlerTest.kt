package digits.shared.inbound.http

import digits.shared.inbound.http.errors.ApiErrors
import digits.shared.AccessDenied
import digits.shared.inbound.http.errors.ApiErrorConverter
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.context.request.WebRequest

class ExceptionsHandlerTest {
    private val webRequest: WebRequest = mockk()
    private val apiErrorConverter: ApiErrorConverter = mockk()

    private lateinit var sut: ExceptionsHandler

    @BeforeEach
    internal fun setUp() {
        sut = ExceptionsHandler(apiErrorConverter)
    }

    @Test
    fun `Given an access denied exception When handling Then map to forbidden`() {
        // Given
        val exception = AccessDenied(message = "xxx")
        val expectedStatus = HttpStatus.FORBIDDEN
        every { apiErrorConverter.convert(exception) } returns expectedStatus

        // When
        val result = sut.handle(exception, webRequest)

        // Then
        assertThat(result.statusCode).isEqualTo(expectedStatus)
        val body = result.body as ApiErrors
        assertThat(body.errors.size).isEqualTo(1)
        assertThat(body.errors.first().detail).isEqualTo(exception.message)
    }

    @Test
    fun `Given any exception When handling Then should hide exception details`() {
        // Given
        val exception = Exception("fff")

        // When
        val result = sut.handle(exception, webRequest)

        // Then
        assertThat(result.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        val body = result.body as ApiErrors
        assertThat(body.errors.size).isEqualTo(1)
        assertThat(body.errors.first().detail).isEmpty()
    }
}