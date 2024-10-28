package digits.auth.inbound.http

import digits.auth.UserService
import digits.auth.inbound.http.AuthApi
import digits.shared.security.JwtTokenProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication

class AuthApiTest {

    private val authenticationManager: AuthenticationManager = mockk(relaxed = true)
    private val jwtTokenProvider: JwtTokenProvider = mockk(relaxed = true)
    private val userService: UserService = mockk(relaxed = true)

    private val authApi = AuthApi(authenticationManager, jwtTokenProvider, userService)

    @Test
    fun login() {
        // Given
        val loginDto = AuthApi.LoginDto(
            password = "password123",
            username = "testUser"
        )
        val authentication: Authentication = mockk()
        every {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken("testUser", "password123"))
        } returns authentication
        every { authentication.name } returns "testUser"
        every { jwtTokenProvider.generateToken("testUser") } returns "token"

        // When
        val result = authApi.login(loginDto)

        // Then
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo("token")
    }

    @Test
    fun `should register user successfully`() {
        // Given
        val registrationDto = AuthApi.RegistrationDto(
            username = "testUser",
            password = "password123",
            email = "user@user.com"
        )

        // When
        val result = authApi.register(registrationDto)

        // Then
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo("User registered successfully")
        verify { userService.createUser("testUser", "password123", "user@user.com") }
    }
}