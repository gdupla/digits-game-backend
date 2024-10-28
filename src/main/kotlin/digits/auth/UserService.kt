package digits.auth

import digits.shared.NotFound
import java.util.UUID
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
): UserDetailsService {

    fun createUser(name: String, password: String, email: String) {
        val user = User(
            name = name,
            email = email,
            encodedPassword = passwordEncoder.encode(password)
        )
        userRepository.createOrUpdate(user)
    }

    fun getUserFromUsername(username: String): User {
        return userRepository.getUserFromUsername(username) ?:  throw NotFound("User $username was not found.")
    }

    override fun loadUserByUsername(userId: String): UserDetails {
        val user = userRepository.getUser(User.Id(UUID.fromString(userId)))
            ?: throw NotFound("User $userId was not found.")
        // Return a UserDetails object
        return org.springframework.security.core.userdetails.User(
            user.name,
            user.encodedPassword,
            true,
            true,
            true,
            true,
            emptyList() // Assuming you have a method to convert roles to GrantedAuthorities
        )
    }
}