package adventofcode

import tool.mylambdas.splitByCondition

fun main() {
    Day06(test=false).showResult()
}

/**
 * I had hard part with the second part of this one, since the trailing spaces in the input, are not 'saved'
 * in my data files (example, input) by IntelliJ. So, I had to add them programatically.
 */

class Day06(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Trash Compactor", hasInputFile = true) {

    override fun resultPartOne(): Any {
        val mathNumbers = inputLines
            .dropLast(1)
            .map { line ->
                line.trim().split("\\s+".toRegex()).map {it.toLong()}
            }
        val operators = inputLines.last().trim().split("\\s+".toRegex())

        val x = mathNumbers
            .transpose()
            .withIndex()
            .sumOf { it.value.function(operators[it.index])}
        return x
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

    private fun List<List<Long>>.transpose(): List<List<Long>> {
        val transposedMatrix = mutableListOf<List<Long>>()
        for (col in this.first().indices) {
            val newRow = mutableListOf<Long>()
            for (row in this.indices) {
                newRow += this[row][col]
            }
            transposedMatrix += newRow
        }
        return transposedMatrix
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
