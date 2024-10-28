package digits.game.inbound.http

import digits.game.Game
import digits.game.GameService
import digits.auth.User
import digits.shared.security.JwtTokenProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GameApiTest {

    private lateinit var gameService: GameService
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var gameApi: GameApi

    @BeforeEach
    fun setUp() {
        gameService = mockk()
        jwtTokenProvider = mockk()
        gameApi = GameApi(gameService, jwtTokenProvider)
    }


    @Test
    fun `createGame should create a game and return response`() {
        // Given
        val gameName = "New Game"
        val userId = UUID.randomUUID()
        val token = "Bearer someToken"
        val game = Game(Game.Id.generate(), gameName, User.Id(userId))

        every { jwtTokenProvider.extractUserId("someToken") } returns userId.toString()
        every { gameService.createGame(gameName, User.Id(userId)) } returns game

        // When
        val response = gameApi.createGame(token, gameName)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(GameResponse(game))
        verify { jwtTokenProvider.extractUserId("someToken") }
        verify { gameService.createGame(gameName, User.Id(userId)) }
    }

    @Test
    fun `getGames should return list of games`() {
        // Given
        val game1 = Game(Game.Id.generate(), "Game 1", User.Id(UUID.randomUUID()))
        val game2 = Game(Game.Id.generate(), "Game 2", User.Id(UUID.randomUUID()))
        val games = listOf(game1, game2)

        every { gameService.getGames() } returns games

        // When
        val response = gameApi.getGames()

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).containsExactlyInAnyOrder(GameResponse(game1), GameResponse(game2))
        verify { gameService.getGames() }
    }

    @Test
    fun `getGame should return a specific game`() {
        // Given
        val gameId = UUID.randomUUID()
        val game = Game(Game.Id(gameId), "Game", User.Id(UUID.randomUUID()))

        every { gameService.getGame(Game.Id(gameId)) } returns game

        // When
        val response = gameApi.getGame(gameId)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(GameDetailResponse(game))
        verify { gameService.getGame(Game.Id(gameId)) }
    }

    @Test
    fun `addSecondPlayer should add a second player and return game details`() {
        // Given
        val gameId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val token = "Bearer someToken"
        val game = Game(Game.Id(gameId), "Game", User.Id(userId))

        every { jwtTokenProvider.extractUserId("someToken") } returns userId.toString()
        every { gameService.addSecondPlayer(Game.Id(gameId), User.Id(userId)) } returns game

        // When
        val response = gameApi.addSecondPlayer(token, gameId)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(GameDetailResponse(game))
        verify { jwtTokenProvider.extractUserId("someToken") }
        verify { gameService.addSecondPlayer(Game.Id(gameId), User.Id(userId)) }
    }

    @Test
    fun `placeNumber should place a number on the game board and return game details`() {
        // Given
        val gameId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val token = "Bearer someToken"
        val placeNumberRequest = GameApi.PlaceNumberRequest(1, 2, 3)
        val game = Game(Game.Id(gameId), "Game", User.Id(userId))

        every { jwtTokenProvider.extractUserId("someToken") } returns userId.toString()
        every { gameService.placeNumber(Game.Id(gameId), User.Id(userId), 1, 2, 3) } returns game

        // When
        val response = gameApi.placeNumber(token, gameId, placeNumberRequest)

        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(GameDetailResponse(game))
        verify { jwtTokenProvider.extractUserId("someToken") }
        verify { gameService.placeNumber(Game.Id(gameId), User.Id(userId), 1, 2, 3) }
    }
}