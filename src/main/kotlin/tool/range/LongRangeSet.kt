package tool.range

import kotlin.math.max
import kotlin.math.min

data class LongRangeSet private constructor (val disjunctRanges: Set<LongRange>) {

    companion object {
        fun of(initialRangeList: List<LongRange> = emptyList()): LongRangeSet {
            return LongRangeSet(initialRangeList.fold(emptySet()) { acc, r -> acc.combine(r) } )
        }

        private fun Set<LongRange>.combine(range: LongRange): Set<LongRange> {
            val newTotal = mutableSetOf<LongRange>()
            var growingRange = range
            this.sortedBy { it.first }.forEach { listedRange ->
                if (listedRange.hasOverlap(growingRange)) {
                    growingRange = growingRange.combine(listedRange)
                } else {
                    newTotal += listedRange
                }
            }
            newTotal += growingRange
            return newTotal
        }

        private fun LongRange.hasOverlap(other: LongRange): Boolean =
            !(other.last < this.first || other.first > this.last)

        private fun LongRange.combine(other: LongRange) : LongRange =
            min(this.first, other.first) .. max(this.last, other.last)

        private fun Set<LongRange>.areDisjunct(): Boolean {
            return false
        }
    }

    fun plusAll(rangeList: List<LongRange>): LongRangeSet {
        return LongRangeSet(rangeList.fold(disjunctRanges) { acc, r -> acc.combine(r) } )
    }

    fun plusRange(range: LongRange): LongRangeSet {
        return LongRangeSet(disjunctRanges.combine(range))
    }

    fun contains(number: Long) = disjunctRanges.any { range -> number in range }

    fun size() = disjunctRanges.sumOf{ range -> range.last - range.first + 1 }
    fun countDisjunctRanges() = disjunctRanges.size
    fun getDisjunctRanges() = disjunctRanges.toList()
}
