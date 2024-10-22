package digits.game

data class GameState(
    val playerOneBoard: Board = Board(),
    val playerTwoBoard: Board = Board(),
    var commonNumbers: List<Int> = emptyList(),
    var nextNumber: Int = 0,
    var playerOneScore: Int = 0,
    var playerTwoScore: Int = 0
)

