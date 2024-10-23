package digits.players

interface PlayerRepository {

    fun createOrUpdate(player: Player)

    fun getPlayer(playerId: Player.Id): Player
}