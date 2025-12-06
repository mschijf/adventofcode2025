package adventofcode

import tool.math.Matrix
import tool.mylambdas.splitByCondition
import kotlin.collections.map
import kotlin.text.trim

fun main() {
    Day06(test=false).showResult()
}

/**
 * I had hard part with the second part of this one, since the trailing spaces in the input, are not 'saved'
 * in my data files (example, input) by IntelliJ. So, I had to add them programmatically.
 *
 * Later I decided to introduce the matrix classes, that helped a lot.
 */

class Day06(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Trash Compactor", hasInputFile = true) {

    override fun resultPartOne(): Any {
        val llLong = inputLines
            .dropLast(1)
            .map { line -> line.trim().split("\\s+".toRegex()).map { str -> str.toLong() }}

        val matrix = Matrix.of(llLong, 0L)

        val operators = inputLines.last().trim().split("\\s+".toRegex())

        return operators
            .withIndex()
            .sumOf { matrix.getCol(it.index).function(it.value) }
    }

    override fun resultPartTwo(): Any {
        val llChar = inputLines
            .dropLast(1)
            .map { it.map {ch -> ch}}
        val matrix = Matrix.of(llChar, ' ')
        val operators = inputLines.last().trim().split("\\s+".toRegex())
        val verticalNumberList = (0..matrix.colCount-1)
            .map { matrix.getCol(it).joinToString("") }
            .splitByCondition { it.isBlank() }
        val mathNumbers = verticalNumberList.map { it.map { columnString -> columnString.toCephalopodNumber()}}

        return operators
            .withIndex()
            .sumOf { mathNumbers[it.index].function( it.value) }
    }

    private fun List<Long>.function(operator: String): Long {
        return when (operator) {
            "*" -> this.fold(1){acc, i -> acc * i}
            "+" -> this.sum()
            else -> throw Exception("WEIRD")
        }
    }

    /**
     * following the examples, we cannot interpret a space with a 0, but we have to ignore it
     */
    private fun String.toCephalopodNumber() : Long {
        return this.replace(" ", "").toLong()
    }
}

//
// Note: CharMatrix and LongMatrix below, replaced by a generic Matrix in the math package
//

//data class CharMatrix private constructor (private val matrix: List<List<Char>>) {
//    // add trailing spaces, to make each line equal length
//    val rowCount: Int = matrix.size
//    val colCount: Int = matrix.first().size
//
//    companion object {
//        fun of (inputLines: List<String>): CharMatrix {
//            val colCount = inputLines.maxOf { it.length }
//            return CharMatrix (inputLines.map { it.padEnd(colCount).toList() } )
//        }
//    }
//
//    operator fun get(row: Int, col: Int): Char = matrix[row][col]
//    operator fun get(row: Int): List<Char> = matrix[row]
//    fun getRow(row: Int): List<Char> = this[row]
//    fun getCol(col: Int): List<Char> = matrix.map { it[col] }
//}
//
//data class LongMatrix private constructor (private val matrix: List<List<Long>>) {
//    val rowCount: Int = matrix.size
//    val colCount: Int = matrix.first().size
//
//    companion object {
//        fun ofLongLists(inputLongList: List<List<Long>>, default: Long = 0L): LongMatrix {
//            val maxCol = inputLongList.maxOf {it.size}
//            return LongMatrix(inputLongList.map { it + List(maxCol - it.size) {default} })
//        }
//
//        fun ofStringLists(inputStringList: List<List<String>>, default: Long = 0L): LongMatrix {
//            val inputLongList = inputStringList.map { line -> line.map { str -> str.toLong() } }
//            return ofLongLists(inputLongList, default)
//        }
//
//        fun ofStringLines(inputStringLines: List<String>, default: Long = 0L): LongMatrix {
//            val inputLongList = inputStringLines.map { line -> line.trim().split("\\s+".toRegex()) }
//            return ofStringLists(inputLongList, default)
//        }
//
//    }
//
//    operator fun get(row: Int, col: Int): Long = matrix[row][col]
//    operator fun get(row: Int): List<Long> = matrix[row]
//    fun getRow(row: Int): List<Long> = this[row]
//    fun getCol(col: Int): List<Long> = matrix.map { it[col] }
//}