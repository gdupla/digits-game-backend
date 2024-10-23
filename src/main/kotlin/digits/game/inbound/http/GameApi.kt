package digits.game.inbound.http

import digits.game.GameService
import digits.game.Game
import digits.players.Player
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
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
    fun createGame(@RequestParam playerOneId: String, @RequestParam playerTwoId: String): ResponseEntity<Game> {
        val game = gameService.createGame(
            Player.Id(UUID.fromString(playerOneId)),
            Player.Id(UUID.fromString(playerTwoId))
        )
        return ResponseEntity.ok(game)
    }

    @GetMapping("/{gameId}")
    fun getGame(@PathVariable gameId: String): ResponseEntity<Game> {
        val game = gameService.getGame(Game.Id(UUID.fromString(gameId)))
        return ResponseEntity.ok(game)
    }

    @PostMapping("/{gameId}/placeNumber")
    fun placeNumber(
        @PathVariable gameId: String,
        @RequestBody placeNumberRequest: PlaceNumberRequest
    ): ResponseEntity<Game> {
        val game = gameService.placeNumber(
            Game.Id(UUID.fromString(gameId)),
            Player.Id(placeNumberRequest.playerId),
            placeNumberRequest.row,
            placeNumberRequest.col,
            placeNumberRequest.number
        )
        return ResponseEntity.ok(game)
    }

    data class PlaceNumberRequest(
        val playerId: UUID,
        val row: Int,
        val col: Int,
        val number: Int
    )
}