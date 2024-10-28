package digits.auth.inbound.http

import digits.auth.UserService
import digits.shared.inbound.http.ExceptionsHandler
import digits.shared.security.JwtTokenProvider
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthApi(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(ExceptionsHandler::class.java)

    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto): ResponseEntity<String> {
        val user = userService.getUserFromUsername(loginDto.username)
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(user.id.value.toString(), loginDto.password)
        )
        val token = jwtTokenProvider.generateToken(user.id.value.toString())
        return ResponseEntity.ok(token)
    }

    @PostMapping("/register")
    fun register(@RequestBody registrationDto: RegistrationDto): ResponseEntity<String> {
        userService.createUser(
            name = registrationDto.username,
            password = registrationDto.password,
            email = registrationDto.email
        )
        return ResponseEntity.ok("User registered successfully")
    }

    data class LoginDto(
        val username: String,
        val password: String
    )

    data class RegistrationDto(
        val username: String,
        val password: String,
        val email: String,
    )

    data class UserDto(
        val id: UUID,
        val username: String,
        val email: String
    )
}