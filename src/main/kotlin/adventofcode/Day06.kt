package adventofcode

import tool.mylambdas.splitByCondition
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
        val matrix = LongMatrix.ofStringLines(inputLines.dropLast(1))
        val operators = inputLines.last().trim().split("\\s+".toRegex())

        return operators
            .withIndex()
            .sumOf { matrix.getCol(it.index).function(it.value) }
    }

    override fun resultPartTwo(): Any {
        val operators = inputLines.last().trim().split("\\s+".toRegex())
        val matrix = CharMatrix(inputLines.dropLast(1))
        val verticalNumberList = matrix.toColList().splitByCondition { it.isBlank() }
        val mathNumbers = verticalNumberList.map { it.map { columnString -> columnString.toCephalopodNumber()}}
        val x = mathNumbers
            .withIndex()
            .sumOf { it.value.function(operators[it.index])}
        return x
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

class CharMatrix (inputLines: List<String>) {
    // add trailing spaces, to make each line equal length
    private val colCount = inputLines.maxOf { it.length }
    private val matrixLines = inputLines.map { it.padEnd(colCount) }

    val rowCount = matrixLines.size

    fun rowString(row: Int): String = matrixLines[row]
    fun colString(col: Int): String = matrixLines.map { it[col] }.joinToString("")

    fun toColList(): List<String> = (0..colCount-1).map { col -> colString(col) }
    fun toRowList(): List<String> = matrixLines
}

data class LongMatrix private constructor (private val matrix: List<List<Long>>) {
    val rowCount: Int = matrix.size
    val colCount: Int = matrix.first().size

    companion object {
        fun ofLongLists(inputLongList: List<List<Long>>, default: Long = 0L): LongMatrix {
            val maxCol = inputLongList.maxOf {it.size}
            return LongMatrix(inputLongList.map { it + List(maxCol - it.size) {default} })
        }

        fun ofStringLists(inputStringList: List<List<String>>, default: Long = 0L): LongMatrix {
            val inputLongList = inputStringList.map { line -> line.map { str -> str.toLong() } }
            return ofLongLists(inputLongList, default)
        }

        fun ofStringLines(inputStringLines: List<String>, default: Long = 0L): LongMatrix {
            val inputLongList = inputStringLines.map { line -> line.trim().split("\\s+".toRegex()) }
            return ofStringLists(inputLongList, default)
        }

    }

    operator fun get(row: Int, col: Int): Long {
        return matrix[row][col]
    }

    operator fun get(row: Int): List<Long> {
        return matrix[row]
    }

    fun getRow(row: Int): List<Long> = this[row]

    fun getCol(col: Int): List<Long> = matrix.map { it[col] }
}