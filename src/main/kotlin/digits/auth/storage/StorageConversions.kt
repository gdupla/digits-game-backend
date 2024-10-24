package digits.auth.storage

import digits.auth.User

fun User.toEntity() =
    UserEntity(
        id = this.id.value,
        name = this.name,
        email = this.email,
        encodedPassword = this.encodedPassword
    )

fun UserEntity.toUser() =
    User(
        id = User.Id(this.id!!),
        name = this.name!!,
        email = this.email!!,
        encodedPassword = this.encodedPassword!!
    )
