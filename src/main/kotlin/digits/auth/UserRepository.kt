package digits.auth

interface UserRepository {

    fun createOrUpdate(user: User)

    fun getUser(userId: User.Id): User?

    fun getUserFromUsername(username: String): User?
}