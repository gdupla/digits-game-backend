package digits.auth

import java.util.UUID

data class User(
    val id: Id = Id.generate(),
    val name: String,
    val email: String,
    val encodedPassword: String
) {
    data class Id(val value: UUID) {
        companion object {
            fun generate() = Id(UUID.randomUUID())
        }
    }
}