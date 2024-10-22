package digits.game

data class Board(
    val cells: Array<Array<Int>> = Array(size = 5) { Array(size = 5) { 0 } }
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        return cells.contentDeepEquals(other.cells)
    }

    override fun hashCode(): Int {
        return cells.contentDeepHashCode()
    }

}