package digits.game.storage

import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class GameEntity(
    @Id
    val id: UUID?,

    @ElementCollection
    var players: MutableList<UUID> = mutableListOf(),

    @ElementCollection
    var playerOneBoard: MutableList<Int> = mutableListOf(),

    @ElementCollection
    var playerTwoBoard: MutableList<Int> = mutableListOf(),

    var nextNumber: Int?,

    @ElementCollection
    var commonNumbers: MutableList<Int> = mutableListOf(),

    var playerOneScore: Int?,

    var playerTwoScore: Int?,

    var nextPlayerId: UUID?,

    var isFinished: Boolean?
)