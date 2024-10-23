package digits.game.storage

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameEntityRepository : JpaRepository<GameEntity, UUID>