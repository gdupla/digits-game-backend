package digits.players.storage

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class PlayerEntity(
    @Id
    val id: UUID?,

    val name: String?,
)