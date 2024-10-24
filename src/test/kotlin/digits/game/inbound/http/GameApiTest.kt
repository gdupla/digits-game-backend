package digits.game.inbound.http

import digits.game.Game
import digits.game.GameService
import digits.auth.User
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GameApiTest {

    private lateinit var gameService: GameService
    private lateinit var gameApi: GameApi

    @BeforeEach
    fun setup() {
        gameService = mockk(relaxed = true)
        gameApi = GameApi(gameService)
    }

    @Test
    fun createGame() {
        // Given
        val userOneId = User.Id.generate()
        val userTwoId = User.Id.generate()
        val game = Game(
            players = mutableListOf(userOneId, userTwoId)
        )

        every { gameService.createGame(userOneId, userTwoId) } returns game

        // When
        val result = gameApi.createGame(userOneId.value.toString(), userTwoId.value.toString())

        // Then
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body!!).isEqualTo(GameResponse(game))
    }

    @Test
    fun getGame() {
        // Given
        val gameId = Game.Id.generate()
        val game = Game(id = gameId)

        every { gameService.getGame(gameId) } returns game

        // When
        val result = gameApi.getGame(gameId.value.toString())

        // Then
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body!!).isEqualTo(GameResponse(game))
    }

    @Test
    fun placeNumber() {
        // Given
        val userOneId = User.Id.generate()
        val gameId = Game.Id.generate()
        val game = Game(id = gameId)

        every { gameService.placeNumber(gameId, userOneId, 0 ,0, 1) } returns game

        // When
        val result = gameApi.placeNumber(
            gameId.value.toString(),
            placeNumberRequest = GameApi.PlaceNumberRequest(userOneId.value, 0, 0, 1))

        // Then
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body!!).isEqualTo(GameResponse(game))
    }
}