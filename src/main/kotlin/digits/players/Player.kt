package digits.players

import java.util.UUID

data class Player(
    val id: Id = Id.generate(),
    val name: String,
) {
    data class Id(val value: UUID) {
        companion object {
            fun generate() = Id(UUID.randomUUID())
        }
    }
}