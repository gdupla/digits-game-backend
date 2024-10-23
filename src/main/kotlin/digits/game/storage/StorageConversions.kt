package digits.game.storage

import digits.game.Board
import digits.game.Game
import digits.players.Player

fun Game.toEntity() =
    GameEntity(
        id = this.id.value,
        players = this.players.map { it.value }.toMutableList(),
        playerOneBoard = this.playerOneBoard.cells.flatMap { it.map { it } }.toMutableList(),
        playerTwoBoard = this.playerTwoBoard.cells.flatMap { it.map { it } }.toMutableList(),
        nextNumber = this.nextNumber,
        commonNumbers = this.commonNumbers.toMutableList(),
        playerOneScore = this.playerOneScore,
        playerTwoScore = this.playerTwoScore,
        nextPlayerId = this.nextPlayerId?.value,
        isFinished = this.isFinished
    )

fun GameEntity.toGame() =
    Game(
        id = Game.Id(this.id!!),
        playerOneBoard = this.playerOneBoard.toBoard(),
        playerTwoBoard = this.playerTwoBoard.toBoard(),
        players = this.players.map { Player.Id(it) }.toMutableList(),
        nextPlayerId = Player.Id(this.nextPlayerId!!),
        commonNumbers = this.commonNumbers,
        nextNumber = this.nextNumber!!,
        playerOneScore = this.playerOneScore!!,
        playerTwoScore = this.playerTwoScore!!,
        isFinished = this.isFinished!!
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
