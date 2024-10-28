package digits.game.inbound.http

import digits.auth.User
import digits.game.Game
import digits.game.GameService
import digits.shared.inbound.http.ExceptionsHandler
import digits.shared.security.JwtTokenProvider
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/games")
class GameApi(
    val gameService: GameService,
    val jwtTokenProvider: JwtTokenProvider
) {
    private val log = LoggerFactory.getLogger(ExceptionsHandler::class.java)

    @PostMapping("/create")
    fun createGame(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam gameName: String
    ): ResponseEntity<GameResponse> {
        log.info("Here we are")
        val token: String = authHeader.substring(7) // Remove "Bearer " from the token
        val userId: String = jwtTokenProvider.extractUserId(token)

        val game = gameService.createGame(
            gameName,
            User.Id(UUID.fromString(userId))
        )
        return ResponseEntity.ok(GameResponse(game))
    }

    @GetMapping()
    fun getGames(): ResponseEntity<List<GameResponse>> {
        val games = gameService.getGames()
        return ResponseEntity.ok(games.map { GameResponse(it) })
    }

    @GetMapping("/{gameId}")
    fun getGame(@PathVariable gameId: UUID): ResponseEntity<GameDetailResponse> {
        val game = gameService.getGame(Game.Id(gameId))
        return ResponseEntity.ok(GameDetailResponse(game))
    }

    @PostMapping("/{gameId}/addSecondPlayer")
    fun addSecondPlayer(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable gameId: UUID
    ): ResponseEntity<GameDetailResponse> {
        val token: String = authHeader.substring(7) // Remove "Bearer " from the token
        val secondPlayerId: String = jwtTokenProvider.extractUserId(token)

        val game = gameService.addSecondPlayer(Game.Id(gameId), User.Id(UUID.fromString(secondPlayerId)))
        return ResponseEntity.ok(GameDetailResponse(game))
    }

    @PostMapping("/{gameId}/placeNumber")
    fun placeNumber(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable gameId: UUID,
        @RequestBody placeNumberRequest: PlaceNumberRequest
    ): ResponseEntity<GameDetailResponse> {
        val token: String = authHeader.substring(7) // Remove "Bearer " from the token
        val playerId: String = jwtTokenProvider.extractUserId(token)

        val game = gameService.placeNumber(
            Game.Id(gameId),
            User.Id(UUID.fromString(playerId)),
            placeNumberRequest.row,
            placeNumberRequest.col,
            placeNumberRequest.number
        )
        return ResponseEntity.ok(GameDetailResponse(game))
    }

    data class PlaceNumberRequest(
        val row: Int,
        val col: Int,
        val number: Int
    )
}