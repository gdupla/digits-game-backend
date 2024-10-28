package digits.game.inbound.http

import digits.game.Game
import java.util.UUID

data class GameDetailResponse(
    val id: UUID,
    val name: String,
    val creator: UUID,
    val playerOneBoard: Array<Array<Int>>,
    val playerTwoBoard: Array<Array<Int>>,
    val players: List<UUID>,
    var nextPlayerId: UUID?,
    var commonNumbers: List<Int>,
    var nextNumber: Int,
    var playerOneScore: Int,
    var playerTwoScore: Int,
    var status: String
) {
    constructor(game: Game) : this(
        id = game.id.value,
        name = game.name,
        creator = game.creator.value,
        playerOneBoard = game.playerOneBoard.cells,
        playerTwoBoard = game.playerTwoBoard.cells,
        players = game.players.map { it.value },
        nextPlayerId = game.nextUserId?.value,
        commonNumbers = game.commonNumbers,
        nextNumber = game.nextNumber,
        playerOneScore = game.playerOneScore,
        playerTwoScore = game.playerTwoScore,
        status = game.status.name
    )
}