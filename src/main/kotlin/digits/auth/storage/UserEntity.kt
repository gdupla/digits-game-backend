package digits.auth.storage

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class UserEntity(
    @Id
    val id: UUID?,

    val name: String?,

    val email: String?,

    val encodedPassword: String?
)