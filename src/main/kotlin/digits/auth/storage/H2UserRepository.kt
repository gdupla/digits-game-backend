package digits.auth.storage

import digits.auth.User
import digits.auth.UserRepository
import kotlin.jvm.optionals.getOrNull
import org.springframework.stereotype.Repository

@Repository
class H2UserRepository(val userEntityRepository: UserEntityRepository) : UserRepository {
    override fun createOrUpdate(user: User) {
        userEntityRepository.save(user.toEntity())
    }

    override fun getUser(userId: User.Id): User? {
        return userEntityRepository.findById(userId.value).getOrNull()?.toUser()
    }

    override fun getUserFromEmail(email: String): User? {
        return userEntityRepository.findUserEntityByEmail(email)?.toUser()
    }

    override fun getUserFromUsername(username: String): User? {
        return userEntityRepository.findUserEntityByName(username)?.toUser()
    }
}