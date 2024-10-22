package digits.game.inbound.http

import digits.game.GameService
import digits.game.GameState
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/game")
class GameApi(val gameService: GameService) {

    @GetMapping("/state")
    fun getGameState(): ResponseEntity<GameState> {
        return ResponseEntity.ok(gameService.getGameState())
    }

    @PostMapping("/placeNumber")
    fun placeNumber(@RequestBody placeNumberRequest: PlaceNumberRequest): ResponseEntity<GameState> {
        gameService.placeNumber(placeNumberRequest)
        return ResponseEntity.ok(gameService.getGameState())
    }

    data class PlaceNumberRequest(
        val player: Int,
        val row: Int,
        val col: Int,
        val number: Int
    )
}