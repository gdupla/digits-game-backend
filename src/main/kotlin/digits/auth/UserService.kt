package digits.auth

import digits.shared.NotFound
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

    fun getUserFromEmail(email: String): User {
        return userRepository.getUserFromEmail(email) ?: throw NotFound("User with email $email was not found.")
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.getUserFromUsername(username) ?: throw NotFound("User $username was not found.")
        // Return a UserDetails object
        return org.springframework.security.core.userdetails.User(
            user.email,
            user.encodedPassword,
            true,
            true,
            true,
            true,
            emptyList() // Assuming you have a method to convert roles to GrantedAuthorities
        )
    }
}