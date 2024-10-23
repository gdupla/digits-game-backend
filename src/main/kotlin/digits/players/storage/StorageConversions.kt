package digits.players.storage

import digits.players.Player

fun Player.toEntity() =
    PlayerEntity(
        id = this.id.value,
        name = this.name,
    )

fun PlayerEntity.toPlayer() =
    Player(
        id = Player.Id(this.id!!),
        name = this.name!!
    )
