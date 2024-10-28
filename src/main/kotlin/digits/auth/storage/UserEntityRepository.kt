package digits.auth.storage

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserEntityRepository : JpaRepository<UserEntity, UUID> {

    fun findUserEntityByName(name: String): UserEntity?
}