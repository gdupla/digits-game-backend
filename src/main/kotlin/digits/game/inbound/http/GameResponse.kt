package digits.game.inbound.http

import digits.game.Game
import java.util.UUID

data class GameResponse(
    val id: UUID,
    val name: String,
    val creator: UUID,
    val status: String
) {
    constructor(game: Game) : this(
        id = game.id.value,
        name = game.name,
        creator = game.creator.value,
        status = game.status.name
    )
}