package adventofcode

import kotlin.math.absoluteValue

fun main() {
    Day01(test=false).showResult()
}

class Day01(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Secret Entrance", hasInputFile = true) {

    private val input = inputLines.map { (if (it.first() == 'L') -1 else 1) * it.drop(1).toInt() }

    override fun resultPartOne(): Any {
        return input
            .runningFold(50){acc, i -> Math.floorMod( acc + i, 100) }
            .count{ it == 0 }
    }

    override fun resultPartTwo(): Any {
        var count0 = 0
        var position = 50
        input.forEach {
            count0 += it.absoluteValue / 100
            val effectiveClicks = it % 100
            val nextPos = position + effectiveClicks
            if (position != 0 && nextPos <= 0 || nextPos >= 100)
                count0++
            position = Math.floorMod( nextPos, 100)
        }
        return count0
    }
}


