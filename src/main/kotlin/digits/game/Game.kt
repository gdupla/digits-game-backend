package digits.game

import digits.players.Player
import java.util.UUID

data class Game(
    val id: Id = Id.generate(),
    val playerOneBoard: Board = Board(),
    val playerTwoBoard: Board = Board(),
    val players: MutableList<Player.Id> = mutableListOf(),
    var nextPlayerId: Player.Id? = null,
    var commonNumbers: List<Int> = emptyList(),
    var nextNumber: Int = 0,
    var playerOneScore: Int = 0,
    var playerTwoScore: Int = 0,
    var isFinished: Boolean = false
) {
    data class Id(val value: UUID) {
        companion object {
            fun generate() = Id(UUID.randomUUID())
        }
    }
}

