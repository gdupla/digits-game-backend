package digits.shared.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() } // Disable CSRF for simplicity (consider enabling it for production)
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/auth/**").permitAll() // Allow public access to authentication endpoints
                    .anyRequest().authenticated() // Require authentication for all other requests
            }
            .oauth2Login { oauth2Login ->
                oauth2Login // Configure OAuth2 login for Google
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java) // Add JWT filter

        return http.build()
    }

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun clientRegistrationRepository(): InMemoryClientRegistrationRepository {
        val googleClientRegistration = ClientRegistration.withRegistrationId("google")
            .clientId("your-google-client-id") // Use the GitHub secret here
            .clientSecret("your-google-client-secret") // Use the GitHub secret here
            .scope("profile", "email")
            .authorizationUri("https://accounts.google.com/o/oauth2/auth")
            .tokenUri("https://oauth2.googleapis.com/token")
            .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
            .userNameAttributeName("sub")
            .redirectUri("http://localhost:8080/login/oauth2/code/google") // Your static redirect URI
            .clientName("Google")
            .build()

        return InMemoryClientRegistrationRepository(googleClientRegistration)
    }
}