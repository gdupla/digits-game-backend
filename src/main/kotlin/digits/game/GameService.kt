package digits.game

import digits.game.inbound.http.GameApi
import org.springframework.stereotype.Service

@Service
class GameService {

    private val gameState = GameState(
        nextNumber = generateNextNumber(),
        commonNumbers = listOf(generateNextNumber(), generateNextNumber(), generateNextNumber())
    )

    fun getGameState(): GameState {
        return gameState
    }

    fun generateInitGameState(): GameState {
        return gameState
    }

    fun placeNumber(request: GameApi.PlaceNumberRequest) {
        if (request.player != gameState.nextPlayerToPlay) {
            throw Exception("Wrong player")
        }
        if (request.row > 4 || request.row < 0) {
            throw Exception("Wrong row")
        }
        if (request.col > 4 || request.col < 0) {
            throw Exception("Wrong column")
        }
        if (!gameState.commonNumbers.contains(request.number)) {
            throw Exception("Wrong number")
        }
        val board = if (request.player == 1) {
            gameState.playerOneBoard
        } else {
            gameState.playerTwoBoard
        }
        val row = request.row
        val col = request.col
        val number = request.number

        board.cells[row][col] = number

        val connections = calculateConnections(board, row, col, number)
        val score = connections * number
        if (request.player == 1) {
            gameState.playerOneScore += score
        } else {
            gameState.playerOneScore += score
        }

        gameState.commonNumbers -= number
        gameState.commonNumbers += gameState.nextNumber
        gameState.nextNumber = generateNextNumber()
        if (gameState.nextPlayerToPlay == 1) {
            gameState.nextPlayerToPlay = 2
        } else {
            gameState.nextPlayerToPlay = 1
        }
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

    // Additional methods to handle game logic
}