package digits.players.storage

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerEntityRepository : JpaRepository<PlayerEntity, UUID>