package digits.game.inbound.http

import digits.game.Game
import java.util.UUID

data class GameResponse(
    val id: UUID,
    val playerOneBoard: Array<Array<Int>>,
    val playerTwoBoard: Array<Array<Int>>,
    val players: List<UUID>,
    var nextPlayerId: UUID?,
    var commonNumbers: List<Int>,
    var nextNumber: Int,
    var playerOneScore: Int,
    var playerTwoScore: Int,
    var isFinished: Boolean
) {
    constructor(game: Game) : this(
        id = game.id.value,
        playerOneBoard = game.playerOneBoard.cells,
        playerTwoBoard = game.playerTwoBoard.cells,
        players = game.players.map { it.value },
        nextPlayerId = game.nextPlayerId?.value,
        commonNumbers = game.commonNumbers,
        nextNumber = game.nextNumber,
        playerOneScore = game.playerOneScore,
        playerTwoScore = game.playerTwoScore,
        isFinished = game.isFinished
    )
}