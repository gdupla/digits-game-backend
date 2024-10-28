package digits.game.storage

import digits.game.Board
import digits.game.Game
import digits.auth.User

fun Game.toEntity() =
    GameEntity(
        id = this.id.value,
        name = this.name,
        creator = this.creator.value,
        players = this.players.map { it.value }.toMutableList(),
        playerOneBoard = this.playerOneBoard.cells.flatMap { it.map { it } }.toMutableList(),
        playerTwoBoard = this.playerTwoBoard.cells.flatMap { it.map { it } }.toMutableList(),
        nextNumber = this.nextNumber,
        commonNumbers = this.commonNumbers.toMutableList(),
        playerOneScore = this.playerOneScore,
        playerTwoScore = this.playerTwoScore,
        nextPlayerId = this.nextUserId?.value,
        status = this.status.name
    )

fun GameEntity.toGame() =
    Game(
        id = Game.Id(this.id!!),
        name = this.name!!,
        creator = User.Id(this.creator!!),
        playerOneBoard = this.playerOneBoard.toBoard(),
        playerTwoBoard = this.playerTwoBoard.toBoard(),
        players = this.players.map { User.Id(it) }.toMutableList(),
        nextUserId = User.Id(this.nextPlayerId!!),
        commonNumbers = this.commonNumbers,
        nextNumber = this.nextNumber!!,
        playerOneScore = this.playerOneScore!!,
        playerTwoScore = this.playerTwoScore!!,
        status = Game.GameStatus.valueOf(this.status!!)
    )

fun MutableList<Int>.toBoard(): Board {
    if (this.size != 25) {
        throw IllegalArgumentException("Board must contain exactly 25 elements.")
    }

    // Create a 5x5 Array
    val array = Array(5) { Array(5) { 0 } }

    // Fill the array with values from the list
    for (i in this.indices) {
        array[i / 5][i % 5] = this[i]
    }

    return Board(array)
}
