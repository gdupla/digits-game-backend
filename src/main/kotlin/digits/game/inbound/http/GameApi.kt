package digits.game.inbound.http

import digits.game.GameService
import digits.game.Game
import digits.auth.User
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/game")
class GameApi(val gameService: GameService) {
    @PostMapping("/create")
    fun createGame(@RequestParam playerOneId: String, @RequestParam playerTwoId: String): ResponseEntity<GameResponse> {
        val game = gameService.createGame(
            User.Id(UUID.fromString(playerOneId)),
            User.Id(UUID.fromString(playerTwoId))
        )
        return ResponseEntity.ok(GameResponse(game))
    }

    @GetMapping("/{gameId}")
    fun getGame(@PathVariable gameId: String): ResponseEntity<GameResponse> {
        val game = gameService.getGame(Game.Id(UUID.fromString(gameId)))
        return ResponseEntity.ok(GameResponse(game))
    }

    @PostMapping("/{gameId}/placeNumber")
    fun placeNumber(
        @PathVariable gameId: String,
        @RequestBody placeNumberRequest: PlaceNumberRequest
    ): ResponseEntity<GameResponse> {
        val game = gameService.placeNumber(
            Game.Id(UUID.fromString(gameId)),
            User.Id(placeNumberRequest.playerId),
            placeNumberRequest.row,
            placeNumberRequest.col,
            placeNumberRequest.number
        )
        return ResponseEntity.ok(GameResponse(game))
    }

    data class PlaceNumberRequest(
        val playerId: UUID,
        val row: Int,
        val col: Int,
        val number: Int
    )
}