package adventofcode

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

    private fun List<Long>.function(operator: String): Long {
        return when (operator) {
            "*" -> this.fold(1){acc, i -> acc * i}
            "+" -> this.sum()
            else -> throw Exception("WEIRD")
        }
    }


    override fun resultPartTwo(): Any {
        // add trailing spaces
        val maxLineLength = inputLines.maxOf { it.length}
        var operatorLine = inputLines.last().padEnd(maxLineLength)
        val numberLines = inputLines.dropLast(1).map {it.padEnd(maxLineLength)}.toMutableList()

        var total = 0L
        while (operatorLine.isNotEmpty()) {
            val operator = operatorLine[0]
            val newLength = operatorLine.determineNumberLength()

            total += numberLines.functionPart2(operator, newLength)

            operatorLine = operatorLine.drop(newLength+1)
            for (i in numberLines.indices) {
                numberLines[i] = numberLines[i].drop(newLength+1)
            }
        }


        return total
    }

    private fun String.determineNumberLength(): Int {
        assert (this[0] == '*' || this[0] == '+')
        var i = 1
        while (i < this.length && this[i] == ' ')
            i++
        return if (i >= this.length) i else i-1
    }

    private fun List<String>.functionPart2(operator: Char, newLength: Int): Long {
        val numberList = this.map{it.take(newLength)}

        val newList = numberList.rightToLeft()
        return when (operator) {
            '*' -> newList.fold(1){acc, i -> acc * i}
            '+' -> newList.sum()
            else -> throw Exception("WEIRD")
        }
    }

    private fun List<String>.rightToLeft(): List<Long> {
        val newList = mutableListOf<Long>()
        for (i in this.first().length-1 downTo 0) {
            var newNumber = 0L
            for (row in this.indices) {
                if (this[row][i] != ' ')
                    newNumber = 10 * newNumber + this[row][i].digitToIntOrNull()!!
            }
            newList += newNumber
        }
        return newList
    }

    private fun String.getDigit(i: Int): Int {
        return if (i < this.length) this[i].digitToIntOrNull()!! else 0
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
}


