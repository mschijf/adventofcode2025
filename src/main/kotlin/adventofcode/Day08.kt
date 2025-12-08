package adventofcode

import tool.coordinate.threedimensional.Point3DLong
import tool.mylambdas.collectioncombination.toCombinedItemsList

fun main() {
    Day08(test=false).showResult()
}

class Day08(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Playground", hasInputFile = true) {
    private val coordinateList = inputLines.map{ Point3DLong.of(it) }
    private val coordinatePairs = coordinateList.toCombinedItemsList().sortedBy { it.first.eucledianDistance(it.second) }

    override fun resultPartOne(): Any {
        val requestedConnections = if (test) 10 else 1000

        val circuitList = mutableListOf<MutableSet<Point3DLong>>()
        coordinatePairs.take(requestedConnections).forEach { coordinatePair ->
            circuitList.addPair(coordinatePair)
        }
        val (a, b, c) = circuitList.map { it.size.toLong() }.sortedDescending().take(3)
        return a*b*c
    }

    override fun resultPartTwo(): Any {
        val circuitList = mutableListOf<MutableSet<Point3DLong>>()
        var idx = 0
        var pair = coordinatePairs[0]
        while (circuitList.sumOf { it.size } < coordinateList.size) {
            pair = coordinatePairs[idx]
            circuitList.addPair(pair)
            idx++
        }
        return pair.first.x * pair.second.x
    }


    private fun MutableList<MutableSet<Point3DLong>>.addPair(pair: Pair<Point3DLong, Point3DLong>) {
        val circuit1 = this.firstOrNull { pair.first in it }
        val circuit2 = this.firstOrNull { pair.second in it }
        if (circuit1 != null && circuit2 != null && circuit1 != circuit2) {
            circuit1.addAll(circuit2)
            this.remove(circuit2)
        } else if (circuit1 == null && circuit2 == null) {
            this.add(mutableSetOf(pair.first, pair.second))
        } else if (circuit1 != null) {
            circuit1.add(pair.second)
        } else if (circuit2 != null) {
            circuit2.add(pair.first)
        } else {
            //impossible
        }
    }

}






