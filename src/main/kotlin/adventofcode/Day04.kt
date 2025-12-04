package adventofcode

import tool.coordinate.twodimensional.Point

fun main() {
    Day04(test=false).showResult()
}

class Day04(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Printing Department", hasInputFile = true) {

    private val grid = inputLines.asGrid()

    override fun resultPartOne(): Any {
        val paperRollPositions = grid.filterValues { it == '@' }.keys
        return paperRollPositions.count { prp -> prp.hasLessThanFourNeighbors(paperRollPositions) }
    }

    override fun resultPartTwo(): Any {
        var paperRollPositions = grid.filterValues { it == '@' }.keys
        var removed = 0
        do {
            val canBeRemoved = paperRollPositions.filter { prp -> prp.hasLessThanFourNeighbors(paperRollPositions) }
            paperRollPositions = paperRollPositions - canBeRemoved
            removed += canBeRemoved.size
        } while (canBeRemoved.isNotEmpty())
        return removed
    }

    private fun Point.hasLessThanFourNeighbors(paperRollPositions: Set<Point>): Boolean =
        this.allWindDirectionNeighbors().count { neighbour -> neighbour in paperRollPositions } < 4

}
