package adventofcode

import tool.coordinate.threedimensional.Point3DLong
import tool.mylambdas.collectioncombination.toCombinedItemsList
import kotlin.math.sqrt

fun main() {
    Day08(test=false).showResult()
}

class Day08(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Playground", hasInputFile = true) {
    private val coordinateList = inputLines.map{ Point3DLong.of(it) }

    //96965 --> wrong
    override fun resultPartOne(): Any {
        val repeater = if (test) 10 else 1000
        val coordinatePairs = coordinateList.toCombinedItemsList().sortedBy { it.first.distance(it.second) }

        val circuitSet = mutableListOf<MutableSet<Point3DLong>>()
        repeat(repeater) { idx ->
            val pair = coordinatePairs[idx]
            circuitSet.addPair(pair)
        }
        return circuitSet.map { it.size }.sortedDescending().take(3).fold(1L) { acc, i -> acc * i }
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }

    private fun Point3DLong.distance(other: Point3DLong): Double {
        return sqrt( sqr(this.x - other.x) + sqr(this.y - other.y) + sqr(this.z - other.z) )
    }

    private fun sqr(a: Long): Double = (a*a).toDouble()

    private fun MutableList<MutableSet<Point3DLong>>.addPair(pair: Pair<Point3DLong, Point3DLong>) {
        val circuit1 = this.firstOrNull { pair.first in it }
        val circuit2 = this.firstOrNull { pair.second in it }
        if (circuit1 != null && circuit2 != null) {
            if (circuit1 != circuit2) {
                circuit1.addAll(circuit2)
                this.remove(circuit2)
            } else {
//                println("Same circuit : $pair")
                //both in same circuit --> do nothing
            }
        } else if (circuit1 == null && circuit2 == null) {
            this.add(mutableSetOf(pair.first, pair.second))
        } else {
            if (circuit1 != null) {
                circuit1.add(pair.second)
            } else if (circuit2 != null) {
                circuit2.add(pair.first)
            } else {
                //impossible
            }
        }
    }
}






