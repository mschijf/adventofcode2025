package adventofcode

import tool.mylambdas.splitByCondition
import tool.range.LongRangeSet
import kotlin.collections.map

fun main() {
    Day05(test=false).showResult()
}

class Day05(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Cafeteria", hasInputFile = true) {

    private val input = inputLines.splitByCondition { it.isEmpty() }

    private val freshRangeList = input.first().map { rawLine ->
        rawLine
            .split("-")
            .let { LongRange(it[0].toLong(), it[1].toLong()) }
    }
    private val ingredientList = input.last().map { it.toLong() }

    val rangeSet = LongRangeSet.of(freshRangeList)

    override fun resultPartOne(): Any {
        return ingredientList.count { rangeSet.contains(it) }
    }

    override fun resultPartTwo(): Any {
        return rangeSet.size()
    }
}

//answers
//674
//352509891817881

