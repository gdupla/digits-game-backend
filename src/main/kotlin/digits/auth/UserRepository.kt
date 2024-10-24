package digits.auth

interface UserRepository {

    fun createOrUpdate(user: User)

    fun getUser(userId: User.Id): User?

    fun getUserFromEmail(email: String): User?
    fun getUserFromUsername(username: String): User?
}