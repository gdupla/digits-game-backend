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

    fun getUsernameFromToken(token: String): String {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey) // Ensure to use the same key for validation
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}