package digits.players.storage

import digits.players.Player
import digits.players.PlayerRepository
import org.springframework.stereotype.Repository

@Repository
class H2PlayerRepository(val playerEntityRepository: PlayerEntityRepository) : PlayerRepository {
    override fun createOrUpdate(player: Player) {
        playerEntityRepository.save(player.toEntity())
    }

    override fun getPlayer(playerId: Player.Id): Player {
        return playerEntityRepository.findById(playerId.value).get().toPlayer()
    }
}