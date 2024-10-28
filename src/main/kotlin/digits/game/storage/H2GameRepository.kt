package digits.game.storage

import digits.game.Game
import digits.game.GameRepository
import kotlin.jvm.optionals.getOrNull
import org.springframework.stereotype.Repository

@Repository
class H2GameRepository(val gameEntityRepository: GameEntityRepository): GameRepository {
    override fun createOrUpdate(game: Game) {
        gameEntityRepository.save(game.toEntity())
    }

    override fun findById(gameId: Game.Id): Game? {
        return gameEntityRepository.findById(gameId.value).getOrNull()?.toGame()
    }

    override fun findAll(): List<Game> {
        return gameEntityRepository.findAll().map { it.toGame() }
    }
}