package digits.game

import digits.players.Player
import digits.shared.InvalidArgument
import digits.shared.InvalidState
import digits.shared.NotFound
import org.springframework.stereotype.Service

@Service
class GameService(
    private val gameRepository: GameRepository
) {
    fun createGame(playerOneId: Player.Id, playerTwoId: Player.Id): Game {
        val game = Game(
            nextNumber = generateNextNumber(),
            commonNumbers = listOf(generateNextNumber(), generateNextNumber(), generateNextNumber())
        )

        game.players.add(playerOneId)
        game.players.add(playerTwoId)
        game.nextPlayerId = playerOneId  // Player 1 starts

        gameRepository.createOrUpdate(game)

        return game
    }

    fun getGame(gameId: Game.Id) = gameRepository.findById(gameId) ?: throw NotFound("Game not found.")

    fun placeNumber(gameId: Game.Id, playerId: Player.Id, row: Int, col: Int, number: Int): Game {
        val game = getGame(gameId)

        // Validation: Check if the game is not finished
        if(game.isFinished) {
            throw InvalidState("The game is already finished.")
        }
        // Validation: Check if the player is the next one to play
        if (game.nextPlayerId != playerId) {
            throw InvalidArgument("It's not your turn.")
        }

        // Validation: Ensure row and col are within board limits
        if (row !in 0..4 || col !in 0..4) {
            throw InvalidArgument("Row and column must be between 0 and 4.")
        }

        // Validation: Ensure the number is one of the available common numbers
        if (number !in game.commonNumbers) {
            throw InvalidArgument("Invalid number selection.")
        }

        val board = if (playerId == game.players[0]) {
            game.playerOneBoard
        } else {
            game.playerTwoBoard
        }

        // Validation: Ensure the cell selected to place the number is empty
        if (board.cells[row][col] != 0) {
            throw InvalidArgument("Cell is already filled.")
        }
        board.cells[row][col] = number

        val connections = calculateConnections(board, row, col, number)
        val score = connections * number
        if (playerId == game.players[0]) {
            game.playerOneScore += score
        } else {
            game.playerTwoScore += score
        }

        // Remove placed number and add a new random one
        game.commonNumbers -= number
        game.commonNumbers += game.nextNumber
        game.nextNumber = generateNextNumber()

        // Switch the next player
        game.nextPlayerId = if (game.nextPlayerId == game.players[0]) {
            game.players[1]
        } else {
            game.players[0]
        }

        // Check if the game is finished
        if (isGameFinished(game)) {
            game.isFinished = true
        }
        gameRepository.createOrUpdate(game)

        return game
    }

    private fun isGameFinished(game: Game): Boolean {
        return game.playerOneBoard.cells.flatten().none { it == 0 } &&
                game.playerTwoBoard.cells.flatten().none { it == 0 }
    }

    private fun calculateConnections(board: Board, row: Int, col: Int, number: Int): Int {
        // Directions: (dx, dy) for horizontal, vertical, and diagonal connections
        val directions = listOf(
            Pair(0, 1),  // Right
            Pair(0, -1), // Left
            Pair(1, 0),  // Down
            Pair(-1, 0), // Up
            Pair(1, 1),  // Diagonal Down-Right
            Pair(-1, -1), // Diagonal Up-Left
            Pair(1, -1), // Diagonal Down-Left
            Pair(-1, 1)  // Diagonal Up-Right
        )

        var connections = 0

        for ((dx, dy) in directions) {
            val newRow = row + dx
            val newCol = col + dy

            // Ensure we are within board boundaries and check if the adjacent cell has the same number
            if (newRow in 0..4 && newCol in 0..4 && board.cells[newRow][newCol] == number) {
                connections++
            }
        }

        return connections
    }

    private fun generateNextNumber(): Int {
        val numbers = listOf(1, 2, 2, 3, 3, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 9, 9, 9, 9, 9)
        return numbers.random()
    }
}