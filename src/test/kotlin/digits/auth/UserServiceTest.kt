package digits.auth

import digits.shared.NotFound
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceTest {

    private val passwordEncoder: PasswordEncoder = mockk()
    private val userRepository: UserRepository = mockk(relaxed = true)

    private val userService = UserService(userRepository, passwordEncoder)

    @Test
    fun `should create user successfully`() {
        // Given
        val username = "testUser"
        val password = "password"
        val email = "user@user.com"

        every { passwordEncoder.encode("password") } returns "encodedPassword"

        // When
        userService.createUser(username, password, email)

        // Then
        val userSlot = slot<User>()
        verify { userRepository.createOrUpdate(capture(userSlot)) }
        val userSaved = userSlot.captured
        assertThat(userSaved.name).isEqualTo("testUser")
        assertThat(userSaved.encodedPassword).isEqualTo("encodedPassword")
        assertThat(userSaved.email).isEqualTo("user@user.com")
    }

    @Test
    fun `should load user by username successfully`() {
        // Given
        val username = "testUser"
        val password = "password"
        val email = "user@user.com"
        val user = User(name = username, encodedPassword = password, email = email)

        every { userRepository.getUserFromUsername(username) } returns user

        // Act
        val userDetails = userService.loadUserByUsername(username)

        // Assert
        assertThat(userDetails.username).isEqualTo(email)
        assertThat(userDetails.password).isEqualTo(password) // This will be encoded
        assertThat(userDetails.isEnabled).isTrue
        assertThat(userDetails.isAccountNonExpired).isTrue
        assertThat(userDetails.isAccountNonLocked).isTrue
        assertThat(userDetails.isCredentialsNonExpired).isTrue
        assertThat(userDetails.authorities).isEmpty()

        verify(exactly = 1) { userRepository.getUserFromUsername(username) }
    }

    @Test
    fun `should throw UsernameNotFoundException when user does not exist`() {
        // Given
        val username = "testUser"
        every { userRepository.getUserFromUsername(username) } returns null

        // When & Then
        val exception = assertThrows<NotFound> {
            userService.loadUserByUsername(username)
        }

        assertThat(exception.message).isEqualTo("User testUser was not found.")
    }
}