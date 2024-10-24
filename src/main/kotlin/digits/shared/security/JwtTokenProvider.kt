package digits.shared.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.Date
import org.springframework.stereotype.Component

@Component
class JwtTokenProvider {
    private val expirationTime: Long = 604800000 // 1 week in milliseconds
    private val secretKey = "your-secret-key"

    fun generateToken(username: String): String {
        val claims: Claims = Jwts.claims().setSubject(username)
        val currentDate = Date()
        val expiryDate = Date(currentDate.time + expirationTime)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(currentDate)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }

    fun getUsernameFromToken(token: String): String {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}