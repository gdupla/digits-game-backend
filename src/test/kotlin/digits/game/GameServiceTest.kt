package digits.game

import digits.auth.User
import digits.shared.InvalidArgument
import digits.shared.InvalidState
import digits.shared.NotFound
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows

class GameServiceTest {

    private lateinit var gameRepository: GameRepository
    private lateinit var gameService: GameService

    @BeforeEach
    fun setup() {
        gameRepository = mockk(relaxed = true)
        gameService = GameService(gameRepository)
    }

    @Test
    fun `createGame should create a game`() {
        // Given
        val creatorId = User.Id.generate()
        val gameName = "Game name"

        // When
        val game = gameService.createGame(gameName, creatorId)

        // Then
        assertThat(game).isNotNull
        assertThat(game.name).isEqualTo("Game name")
        assertThat(game.creator).isEqualTo(creatorId)
        assertThat(game.players.size).isEqualTo(1)
        assertThat(game.players[0]).isEqualTo(creatorId)
        assertThat(game.nextUserId).isEqualTo(creatorId)
        assertThat(game.commonNumbers.size).isEqualTo(3)
        assertThat(game.nextNumber).isGreaterThan(0).isLessThan(10)
        assertThat(game.playerOneScore).isEqualTo(0)
        assertThat(game.playerTwoScore).isEqualTo(0)
        assertThat(game.playerOneBoard.cells.flatten().distinct()).containsExactly(0)
        assertThat(game.playerTwoBoard.cells.flatten().distinct()).containsExactly(0)
        assertThat(game.status).isEqualTo(Game.GameStatus.CREATED)

        verify { gameRepository.createOrUpdate(game) }
    }

    @Test
    fun `getGames returns the list of games`() {
        // Given
        val gameOneId = Game.Id.generate()
        val creatorOneId = User.Id.generate()
        val gameOneName = "Game One name"
        val gameOne = Game(id = gameOneId, creator = creatorOneId, name = gameOneName)

        val gameTwoId = Game.Id.generate()
        val creatorTwoId = User.Id.generate()
        val gameTwoName = "Game Two name"
        val gameTwo = Game(id = gameTwoId, creator = creatorTwoId, name = gameTwoName)

        every { gameRepository.findAll() } returns listOf(gameOne, gameTwo)

        // When
        val gamesFound = gameService.getGames()

        // Then
        assertThat(gamesFound).containsExactlyInAnyOrder(gameOne, gameTwo)
    }

    @Test
    fun `getGame returns the game`() {
        // Given
        val gameId = Game.Id.generate()
        val creatorId = User.Id.generate()
        val gameName = "Game name"
        val game = Game(id = gameId, creator = creatorId, name = gameName)

        every { gameRepository.findById(gameId) } returns game

        // When
        val gameFound = gameService.getGame(gameId)

        // Then
        assertThat(gameFound).isEqualTo(game)
    }

    @Test
    fun `getGame throws exception if the game is not found`() {
        // Given
        val gameId = Game.Id.generate()

        every { gameRepository.findById(gameId) } returns null

        // When & Then
        val exception = assertThrows<NotFound> {
            gameService.getGame(gameId) // This gameId doesn't exist
        }
        assertThat(exception.message).isEqualTo("Game not found.")
    }

    @Test
    fun `addPlayer adds a second player and starts the game`() {
        // Given
        val gameId = Game.Id.generate()
        val creatorId = User.Id.generate()
        val gameName = "Game name"
        val game = Game(
            id = gameId,
            creator = creatorId,
            name = gameName,
            players = mutableListOf(creatorId),
            status = Game.GameStatus.CREATED
        )

        every { gameRepository.findById(gameId) } returns game

        val secondPlayerId = User.Id.generate()

        // When
        val result = gameService.addSecondPlayer(gameId, secondPlayerId)

        // Then
        assertThat(result.status).isEqualTo(Game.GameStatus.IN_PROGRESS)
        assertThat(result.players).containsExactlyInAnyOrder(creatorId, secondPlayerId)
    }

    @Test
    fun `addPlayer throws exception when players size is not 1`() {
        // Given
        val gameId = Game.Id.generate()
        val creatorId = User.Id.generate()
        val gameName = "Game name"
        val game = Game(
            id = gameId,
            creator = creatorId,
            name = gameName,
            status = Game.GameStatus.CREATED
        )

        every { gameRepository.findById(gameId) } returns game

        val secondPlayerId = User.Id.generate()

        // When & Then
        val exception = assertThrows<InvalidState> { gameService.addSecondPlayer(gameId, secondPlayerId) }
        assertThat(exception.message).isEqualTo("The game is not configured correctly.")
    }

    @Test
    fun `addPlayer throws exception when status is not CREATED`() {
        // Given
        val gameId = Game.Id.generate()
        val creatorId = User.Id.generate()
        val gameName = "Game name"
        val game = Game(
            id = gameId,
            creator = creatorId,
            name = gameName,
            players = mutableListOf(creatorId),
            status = Game.GameStatus.IN_PROGRESS
        )

        every { gameRepository.findById(gameId) } returns game

        val secondPlayerId = User.Id.generate()

        // When & Then
        val exception = assertThrows<InvalidState> { gameService.addSecondPlayer(gameId, secondPlayerId) }
        assertThat(exception.message).isEqualTo("The game is not waiting for a second player.")
    }

    @Test
    fun `placeNumber should place a number on the board and update score`() {
        // Given
        val userOneId = User.Id.generate()
        val userTwoId = User.Id.generate()
        val gameName = "Game name"

        val game = Game(
            creator = userOneId,
            name = gameName,
            players = mutableListOf(userOneId, userTwoId),
            nextUserId = userOneId,
            commonNumbers = mutableListOf(1, 1, 3),
            nextNumber = 4,
            status = Game.GameStatus.IN_PROGRESS
        )
        every { gameRepository.findById(game.id) } returns game

        // When
        val updatedGameOne = gameService.placeNumber(game.id, userOneId, 0, 0, 1)

        // Then
        assertThat(updatedGameOne.playerOneBoard.cells[0][0]).isEqualTo(1)
        assertThat(updatedGameOne.commonNumbers).contains(4)
        assertThat(updatedGameOne.nextUserId).isEqualTo(userTwoId)
        assertThat(updatedGameOne.playerOneScore).isEqualTo(0)
        assertThat(updatedGameOne.playerTwoScore).isEqualTo(0)

        val nextNumber = updatedGameOne.nextNumber

        verify { gameRepository.createOrUpdate(updatedGameOne) }

        // When Two
        val updatedGameTwo = gameService.placeNumber(game.id, userTwoId, 0, 0, 3)

        // Then Two
        assertThat(updatedGameTwo.playerTwoBoard.cells[0][0]).isEqualTo(3)
        assertThat(updatedGameTwo.commonNumbers).contains(nextNumber)
        assertThat(updatedGameTwo.nextUserId).isEqualTo(userOneId)
        assertThat(updatedGameTwo.playerOneScore).isEqualTo(0)
        assertThat(updatedGameTwo.playerTwoScore).isEqualTo(0)

        val nextNumberTwo = updatedGameTwo.nextNumber

        verify { gameRepository.createOrUpdate(updatedGameTwo) }

        // When Three
        val updatedGameThree = gameService.placeNumber(game.id, userOneId, 1, 0, 1)

        // Then Two
        assertThat(updatedGameThree.playerOneBoard.cells[1][0]).isEqualTo(1)
        assertThat(updatedGameThree.commonNumbers).contains(nextNumberTwo)
        assertThat(updatedGameThree.nextUserId).isEqualTo(userTwoId)
        assertThat(updatedGameThree.playerOneScore).isEqualTo(1)
        assertThat(updatedGameThree.playerTwoScore).isEqualTo(0)

        verify { gameRepository.createOrUpdate(updatedGameThree) }
    }

    @Test
    fun `placeNumber should throw exception if the game is not in progress`() {
        // Given
        val creatorId = User.Id.generate()
        val gameName = "Game name"

        val game = Game(
            creator = creatorId,
            name = gameName,
            players = mutableListOf(creatorId),
            nextUserId = creatorId,
            status = Game.GameStatus.CREATED
        )
        every { gameRepository.findById(game.id) } returns game

        // When & Then
        val exception = assertThrows<InvalidState> {
            gameService.placeNumber(game.id, creatorId, 0, 0, 1) // The game is finished
        }
        assertThat(exception.message).isEqualTo("The game is waiting for a second player.")
    }

    @Test
    fun `placeNumber should throw exception if the game is already finished`() {
        // Given
        val userOneId = User.Id.generate()
        val userTwoId = User.Id.generate()
        val gameName = "Game name"

        val game = Game(
            creator = userOneId,
            name = gameName,
            players = mutableListOf(userOneId, userTwoId),
            nextUserId = userOneId,
            status = Game.GameStatus.FINISHED
        )
        every { gameRepository.findById(game.id) } returns game

        // When & Then
        val exception = assertThrows<InvalidState> {
            gameService.placeNumber(game.id, userOneId, 0, 0, 1) // The game is finished
        }
        assertThat(exception.message).isEqualTo("The game is already finished.")
    }

    @Test
    fun `placeNumber should throw exception if it's not the player's turn`() {
        // Given
        val userOneId = User.Id.generate()
        val userTwoId = User.Id.generate()
        val gameName = "Game name"

        val game = Game(
            creator = userOneId,
            name = gameName,
            players = mutableListOf(userOneId, userTwoId),
            nextUserId = userOneId,
            commonNumbers = mutableListOf(1, 1, 3),
            nextNumber = 4,
            status = Game.GameStatus.IN_PROGRESS
        )
        every { gameRepository.findById(game.id) } returns game

        // When & Then
        val exception = assertThrows<InvalidArgument> {
            gameService.placeNumber(game.id, userTwoId, 0, 0, 1) // Player 2 is not the next to play
        }
        assertThat(exception.message).isEqualTo("It's not your turn.")
    }

    @Test
    fun `placeNumber should throw exception if number is invalid`() {
        // Given
        val userOneId = User.Id.generate()
        val userTwoId = User.Id.generate()
        val gameName = "Game name"

        val game = Game(
            creator = userOneId,
            name = gameName,
            players = mutableListOf(userOneId, userTwoId),
            nextUserId = userOneId,
            commonNumbers = mutableListOf(1, 2, 3),
            nextNumber = 4,
            status = Game.GameStatus.IN_PROGRESS
        )
        every { gameRepository.findById(game.id) } returns game

        // When & Then
        val exception = assertThrows<InvalidArgument> {
            gameService.placeNumber(game.id, userOneId, 0, 0, 5) // Number 5 is not available
        }
        assertThat(exception.message).isEqualTo("Invalid number selection.")
    }

    @Test
    fun `placeNumber should throw exception if row or col it's outside the board`() {
        // Given
        val userOneId = User.Id.generate()
        val userTwoId = User.Id.generate()
        val gameName = "Game name"

        val game = Game(
            creator = userOneId,
            name = gameName,
            players = mutableListOf(userOneId, userTwoId),
            nextUserId = userOneId,
            commonNumbers = mutableListOf(1, 2, 3),
            nextNumber = 4,
            status = Game.GameStatus.IN_PROGRESS
        )
        every { gameRepository.findById(game.id) } returns game

        // When & Then
        val exceptionOne = assertThrows<InvalidArgument> {
            gameService.placeNumber(game.id, userOneId, 5, 0, 1) // Row should be between 0 and 4
        }
        assertThat(exceptionOne.message).isEqualTo("Row and column must be between 0 and 4.")

        val exceptionTwo = assertThrows<InvalidArgument> {
            gameService.placeNumber(game.id, userOneId, -1, 0, 1) // Row should be between 0 and 4
        }
        assertThat(exceptionTwo.message).isEqualTo("Row and column must be between 0 and 4.")

        val exceptionThree = assertThrows<InvalidArgument> {
            gameService.placeNumber(game.id, userOneId, 0, 5, 1) // Col should be between 0 and 4
        }
        assertThat(exceptionThree.message).isEqualTo("Row and column must be between 0 and 4.")

        val exceptionFour = assertThrows<InvalidArgument> {
            gameService.placeNumber(game.id, userOneId, 0, -1, 1) // Col should be between 0 and 4
        }
        assertThat(exceptionFour.message).isEqualTo("Row and column must be between 0 and 4.")
    }

    @Test
    fun `placeNumber should throw exception if the cell is already filled`() {
        // Given
        val userOneId = User.Id.generate()
        val userTwoId = User.Id.generate()
        val gameName = "Game name"

        val game = Game(
            creator = userOneId,
            name = gameName,
            players = mutableListOf(userOneId, userTwoId),
            nextUserId = userOneId,
            commonNumbers = mutableListOf(1, 2, 3),
            nextNumber = 4,
            status = Game.GameStatus.IN_PROGRESS
        )
        every { gameRepository.findById(game.id) } returns game

        gameService.placeNumber(game.id, userOneId, 0, 0, 1)
        gameService.placeNumber(game.id, userTwoId, 0, 0, 2)

        // When & Then
        val exception = assertThrows<InvalidArgument> {
            gameService.placeNumber(game.id, userOneId, 0, 0, 3) // Row 0 and col 0 is already filled
        }
        assertThat(exception.message).isEqualTo("Cell is already filled.")
    }

    @Test
    fun `placeNumber finish the game when both boards are complete`() {
        // Given
        val userOneId = User.Id.generate()
        val userTwoId = User.Id.generate()

        val cellsPlayerOne = Array(5) { Array(5) { 1 } }
        cellsPlayerOne[0][0] = 0

        val gameName = "Game name"

        val game = Game(
            creator = userOneId,
            name = gameName,
            players = mutableListOf(userOneId, userTwoId),
            nextUserId = userOneId,
            commonNumbers = mutableListOf(1, 1, 3),
            nextNumber = 4,
            playerOneBoard = Board(
                cells = cellsPlayerOne
            ),
            playerTwoBoard = Board(
                cells = Array(5) { Array(5) { 1 } }
            ),
            status = Game.GameStatus.IN_PROGRESS
        )
        every { gameRepository.findById(game.id) } returns game

        // When
        val updatedGameOne = gameService.placeNumber(game.id, userOneId, 0, 0, 1)

        // Then
        assertThat(updatedGameOne.status).isEqualTo(Game.GameStatus.FINISHED)
    }
}