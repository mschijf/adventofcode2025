package adventofcode

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.mylambdas.splitByCondition

fun main() {
    Day12(test=false).showResult()
}

class Day12(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Christmas Tree Farm", hasInputFile = true) {

    private val inputParts = inputLines.splitByCondition { it.isEmpty() }
    private val presentList = inputParts.dropLast(1).map { Present.of(it)}
    private val treeList = inputParts.last()


    /**
     * A bit stolen, not 100% found myself, but the hints helped me here.
     *   by checking the minimal space required, we can already find out that a lot of results are not possible
     *   for the remainder, we can do a brute force search --> surprisingly it brought result within 5-6 seconds
     */
    override fun resultPartOne(): Any {

        return treeList.count { tree ->
            val region = tree.substringBefore(":").trim().toRegion()
            val presentsToPlace = tree.substringAfter(":").trim().toPresentList()
            val minimalSpaceRequired = presentsToPlace.sumOf { it.maxPins}

            if (region.size >= minimalSpaceRequired)
               solvable(region, presentsToPlace)
            else
                false
        }
    }

    override fun resultPartTwo(): Any {
        return "NOT NEEDED"
    }

    private fun String.toRegion(): Set<Point> {
        val dimensions = this.split("x").map {it.toInt()}
        return (0..dimensions[0]-1).flatMap { x ->
            (0..dimensions[1]-1).map { y ->
                pos(x,y)
            }
        }.toSet()
    }

    private fun String.toPresentList(): List<Present> {
        val shapeQuantities = this.split(" ").map {it.toInt()}
        val result = shapeQuantities.flatMapIndexed { idx, count -> List(count){presentList.first { it.idx == idx }} }
        return result
    }

    private fun solvable(emptyFields: Set<Point>, piecesToPlace:List<Present>, cache: MutableMap<Set<Point>, Boolean> = mutableMapOf() ): Boolean {

        if (piecesToPlace.isEmpty()) {
            return true
        }
        val key = emptyFields
        if (cache.contains(key))
            return cache[key]!!

        emptyFields.forEach { field ->
            val candidates = findMatchingPieceStateCandidates(piecesToPlace.first(), field, emptyFields)

            candidates.forEach { (piece, pieceState, startField) ->
                val pieceStateFields = pieceState.pointList.map {pieceStatePoint -> startField + pieceStatePoint}
                val possible = solvable(
                    emptyFields - pieceStateFields.toSet(),
                    piecesToPlace - piece
                )
                if (possible) {
                    cache[key] = true
                    return true
                }
            }
        }
        cache[key] = false
        return false
    }

    private fun Point.availableNeigborsCount(emptyFields: Set<Point>): Int {
        return this.neighbors().count{ it in emptyFields }
    }

    private fun findMatchingPieceStateCandidates(present: Present, field: Point, emptyFields: Set<Point>): List<PlacedPiece> {
        if (field.availableNeigborsCount(emptyFields) == 0)
            return emptyList()

        val result = mutableListOf<PlacedPiece>()
        present.presentRotationStateList.forEach { pieceState ->
            pieceState.pointList.forEach { checkPoint ->
                val diff = field - checkPoint
                if (pieceState.pointList.all {(it + diff) in emptyFields} ) {
                    result += PlacedPiece(present, pieceState, diff)
                }
            }
        }
        return result
    }

}

//----------------------------------------------------------------------------------------------------------------------

data class PlacedPiece(val present: Present, val presentRotationState: PresentRotationState, val onBoardField: Point)

data class Present(val idx: Int, val presentRotationStateList: Set<PresentRotationState>) {
    val maxPins = presentRotationStateList.maxOf{it.pointList.size}

    companion object {
        fun of(raw: List<String>): Present {
            val idx = raw.first().dropLast(1).toInt()
            val form = raw.drop(1)
            return Present(
                idx, setOf(
                    PresentRotationState.of(form),
                    PresentRotationState.of(form).rotateRight(),
                    PresentRotationState.of(form).rotateRight().rotateRight(),
                    PresentRotationState.of(form).rotateRight().rotateRight().rotateRight(),
                )
            )
        }
    }
}

data class PresentRotationState(val pointList: Set<Point>) {
    companion object {
        fun of(input: List<String>) : PresentRotationState {
            assert(input.size == 3)
            assert(input[0].length == 3)
            assert(input[1].length == 3)
            assert(input[2].length == 3)

            val pointMap = input
                .flatMapIndexed { y, line ->
                    line.mapIndexed { x, ch -> pos(x,y) to ch  }
                }.toMap()

            return PresentRotationState(pointMap.filter { it.value == '#' }.keys.toSet())
        }
    }

    fun rotateRight() : PresentRotationState {
        return PresentRotationState(this.pointList.map { it.rotateRightAroundPoint(pos(1,1)) }.toSet())
    }
}
