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
        var totalRange = mutableListOf<FreshRange>()
        freshRangeList.sortedBy { it.first }.forEach { freshRange ->
            val newTotal = mutableListOf<FreshRange>()
            var working = freshRange
            totalRange.forEach { total ->
                if (freshRange.hasOverlap(total)) {
                    working = combine(freshRange, total)
                } else {
                    newTotal += total
                }
            }
            newTotal += working
            totalRange = newTotal
        }
        return totalRange.sumOf {it.count()}
    }

    private fun Long.isFresh(): Boolean =
        freshRangeList.any{ it.containsIngredient(this)}

    private fun combine(freshRange1: FreshRange, freshRange2: FreshRange) : FreshRange {
        if (!freshRange1.hasOverlap(freshRange2))
            println("ERRORRRRR")
        return FreshRange(min(freshRange1.first, freshRange2.first), max(freshRange1.last, freshRange2.last))
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

    fun hasOverlap(other: FreshRange): Boolean {
        if (other.last < this.first)
            return false
        if (other.first > this.last)
            return false
        return true
    }

    fun count() = last - first + 1
}
