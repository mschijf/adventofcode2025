package adventofcode

import tool.mylambdas.splitByCondition
import kotlin.collections.map
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day05(test=false).showResult()
}

class Day05(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Cafeteria", hasInputFile = true) {

    private val input = inputLines.splitByCondition { it.isEmpty() }
    private val freshRangeList = input.first().map{FreshRange.of(it)}
    private val ingredientList = input.last().map{it.toLong()}

    override fun resultPartOne(): Any {
        return ingredientList.count { it.isFresh() }
    }

    override fun resultPartTwo(): Any {
        val combinedRanges = freshRangeList.fold(emptyList<FreshRange>()) { acc, freshRange -> acc.insertAndCombineInList(freshRange)}
        return combinedRanges.sumOf { it.count() }
    }

    private fun Long.isFresh(): Boolean =
        freshRangeList.any{ it.containsIngredient(this)}

    private fun List<FreshRange>.insertAndCombineInList(newRange: FreshRange): List<FreshRange> {
        val newTotal = mutableListOf<FreshRange>()
        var growingRange = newRange
        this.forEach { listedRange ->
            if (listedRange.hasOverlap(growingRange)) {
                growingRange = growingRange.combine(listedRange)
            } else {
                newTotal += listedRange
            }
        }
        newTotal += growingRange
        return newTotal
    }

}

data class FreshRange(val first: Long, val last: Long) {
    companion object {
        fun of(raw: String): FreshRange {
            val parts = raw.split("-")
            return FreshRange(parts[0].toLong(), parts[1].toLong())
        }
    }

    fun containsIngredient(ingredient: Long): Boolean =
        ingredient in first..last

    fun hasOverlap(other: FreshRange): Boolean =
        !(other.last < this.first || other.first > this.last)

    fun combine(otherRange: FreshRange) : FreshRange {
        if (!this.hasOverlap(otherRange))
            throw Exception("Cannot combine these two ranges")
        return FreshRange(min(this.first, otherRange.first), max(this.last, otherRange.last))
    }

    fun count() = last - first + 1
}
