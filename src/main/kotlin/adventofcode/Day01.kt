package adventofcode

import tool.coordinate.twodimensional.pos
import kotlin.math.absoluteValue

fun main() {
    Day01(test=false).showResult()
}

class Day01(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

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
            val effectiveClicks = it.absoluteValue % 100
            if (it > 0) {
                if (position != 0 && position + effectiveClicks >= 100)
                    count0++
            } else {
                if (position != 0 && position - effectiveClicks <= 0)
                    count0++
            }
            position = Math.floorMod( position + it, 100)
        }
        return count0
    }
}


