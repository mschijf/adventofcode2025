package adventofcode

fun main() {
    Day04(test=false).showResult()
}

class Day04(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Printing Department", hasInputFile = true) {

    private val grid = inputLines.asGrid()

    override fun resultPartOne(): Any {
        val paperRollPositions = grid.filterValues { it == '@' }.keys
        return paperRollPositions
            .map {prp -> prp.allWindDirectionNeighbors().count { neighbour -> neighbour in paperRollPositions}}
            .count { it < 4 }
    }

    override fun resultPartTwo(): Any {
        var paperRollPositions = grid.filterValues { it == '@' }.keys
        var removed = 0
        do {
            val canBeRemoved = paperRollPositions
                .filter { prp ->
                    prp.allWindDirectionNeighbors().count { neighbour -> neighbour in paperRollPositions } < 4
                }
            paperRollPositions = paperRollPositions - canBeRemoved
            removed += canBeRemoved.size
        } while (canBeRemoved.isNotEmpty())
        return removed
    }
}
