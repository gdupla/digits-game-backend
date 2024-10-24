package digits.shared

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun googleClientId(): String {
        return System.getenv("GOOGLE_CLIENT_ID") ?: "default-client-id"
    }

    @Bean
    fun googleClientSecret(): String {
        return System.getenv("GOOGLE_CLIENT_SECRET") ?: "default-client-secret"
    }
}