package digits.game

interface GameRepository {

    fun createOrUpdate(game: Game)

    fun findById(gameId: Game.Id): Game?

    fun findAll(): List<Game>
}