package digits.shared.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.Date
import org.springframework.stereotype.Component

@Component
class JwtTokenProvider {
    private val expirationTime: Long = 604800000 // 1 week in milliseconds

    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512)

    fun generateToken(username: String): String {
        val claims: Claims = Jwts.claims().setSubject(username)
        val currentDate = Date()
        val expiryDate = Date(currentDate.time + expirationTime)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(currentDate)
            .setExpiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            extractAllClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun extractUserId(token: String): String {
        return extractAllClaims(token).body.subject // Assuming userId is set as the subject
    }

    private fun extractAllClaims(token: String):  io.jsonwebtoken.Jws<Claims> {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey) // Ensure to use the same key for validation
            .build()
            .parseClaimsJws(token)
    }
}