package tool.math

data class Matrix<T> private constructor (private val matrix: List<List<T>>) {
    val rowCount: Int = matrix.size
    val colCount: Int = matrix.first().size

    companion object {
        fun <T> of (inputLines: List<List<T>>, default: T): Matrix<T> {
            val maxCol = inputLines.maxOf { it.size }
            return Matrix (inputLines.map { it + List(maxCol - it.size) {default} } )
        }
    }

    operator fun get(row: Int, col: Int): T = matrix[row][col]
    operator fun get(row: Int): List<T> = matrix[row]
    fun getRow(row: Int): List<T> = this[row]
    fun getCol(col: Int): List<T> = matrix.map { it[col] }
}
