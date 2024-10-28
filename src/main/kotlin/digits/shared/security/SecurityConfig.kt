package digits.shared.security

import digits.shared.inbound.http.ExceptionsHandler
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    private val log = LoggerFactory.getLogger(ExceptionsHandler::class.java)

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) } // Enable CORS
            .csrf { csrf -> csrf.disable() } // Disable CSRF for simplicity (consider enabling it for production)
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow preflight requests
                    .requestMatchers("/auth/**").permitAll() // Allow public access to /auth endpoints
                    .anyRequest().authenticated() // Require authentication for all other requests
            }
            .oauth2Login { oauth2Login ->
                oauth2Login // Configure OAuth2 login for Google
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java) // Add JWT filter

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowCredentials = true
        configuration.addAllowedOrigin("http://localhost:3000") // Allow your frontend origin
        configuration.addAllowedHeader("*") // Allow all headers
        configuration.addAllowedMethod("*") // Allow all methods (GET, POST, PUT, DELETE, OPTIONS)

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration) // Apply CORS settings to all endpoints
        return source
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
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // Specify the grant type
            .clientName("Google")
            .build()

        return InMemoryClientRegistrationRepository(googleClientRegistration)
    }
}