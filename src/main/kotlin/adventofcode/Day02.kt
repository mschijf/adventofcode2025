package adventofcode

fun main() {
    Day02(test=false).showResult()
}

class Day02(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Gift Shop", hasInputFile = true) {


    private val idRangeList = inputLines.first().split(",").map{Range.of(it)}

    override fun resultPartOne(): Any {
        return idRangeList.sumOf { range ->
            range.toIntRange().filter{ number -> number.hasRepeatPattern() }.sum()
        }
    }

    override fun resultPartTwo(): Any {
        return idRangeList.sumOf { range ->
            range.toIntRange().filter{ number -> number.hasRepeatPatternAtLeastTwice() }.sum()
        }
    }
}

fun Long.hasRepeatPattern(): Boolean {
    val s = this.toString()
    return s.length % 2 == 0 && s.take(s.length/2) == s.takeLast(s.length/2)
}

fun Long.hasRepeatPatternAtLeastTwice(): Boolean {
    val s = this.toString()
    for (i in 1..s.length/2) {
        if (s.length % i == 0) {
            val chunks = s.chunked(i)
            if (chunks.all { it == chunks[0] })
                return true
        }
    }
    return false
}


data class Range(val first: Long, val last: Long) {
    companion object {
        fun of(raw: String): Range {
            val parts = raw.split("-")
            return Range(parts[0].toLong(), parts[1].toLong())
        }
    }

    fun toIntRange() = first..last
}

