package adventofcode

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.mylambdas.collectioncombination.filterCombinedItems
import tool.mylambdas.collectioncombination.mapCombinedItems
import kotlin.collections.chunked
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day09(test=false).showResult()
}

class Day09(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val coordinateList = inputLines.map { pos(it) }

    override fun resultPartOne(): Any {
        return coordinateList.mapCombinedItems { first, second -> rectangleArea(first, second) }.max()
    }

    //1398441360 --> too low
    //2208965837 --> too high
    //1498673376 --> correct
    override fun resultPartTwo(): Any {
        val polygon = Polygon(coordinateList)
        return coordinateList
            .filterCombinedItems { first, second -> polygon.containsRectangle(first, second) }
//            .count()
            .maxOf { pair -> rectangleArea(pair.first, pair.second) }
    }

    private fun rectangleArea(first: Point, second: Point): Long {
        return ((first.x - second.x).absoluteValue + 1L) * ((first.y - second.y).absoluteValue + 1L)
    }
}

class Polygon(coordinateList: List<Point>) {
    private val polygonLineList = createPolygonLines(coordinateList)

    private fun createPolygonLines(coordinateList: List<Point>): List<LinePiece> {
        val xCoordinateGroup = coordinateList.groupBy {it.x}
        val yCoordinateGroup = coordinateList.groupBy {it.y}

        val verticalLines = xCoordinateGroup.values.flatMap { value -> value.sortedBy{ it.y }.chunked(2).map { LinePiece.of(it[0], it[1]) }}
        val horizontalLines = yCoordinateGroup.values.flatMap { value -> value.sortedBy{ it.x }.chunked(2).map { LinePiece.of(it[0], it[1]) }}

        return verticalLines + horizontalLines
    }

    fun containsRectangle(first: Point, second: Point): Boolean {
        val upperLeft = pos(min(first.x, second.x), min(first.y, second.y) )
        val lowerRight = pos(max(first.x, second.x), max(first.y, second.y))

        val probablyInPolygon = polygonLineList.none { pLine -> pLine.crossesRectangle(upperLeft, lowerRight) }
        if (probablyInPolygon) {
            //there is no polygon line in the rectangle, but it is still possible that whole rectangle is fully outside the polygon.
            // take the midpoint of the rectangle and count al vertical lines to the right of that point
            // that should be odd, to lie within the polygon.

            if (upperLeft.x == lowerRight.x || upperLeft.y == lowerRight.y)
                return true //rectangle that is actually a line, is always in (or actually on the edge of) the polygon

            val midPoint = pos((upperLeft.x + lowerRight.x) / 2, (upperLeft.y + lowerRight.y) / 2)

            val lines = midPoint.countVerticalLinesToTheLeft()
            if (lines % 2 == 1)
                return true

//            println("$upperLeft -- $lowerRight")
            return false
        } else {
            return false
        }

    }

    private fun LinePiece.crossesRectangle(upperLeft: Point, lowerRight: Point): Boolean {
        return (this.from.x < lowerRight.x && this.to.x > upperLeft.x)
                &&
                (this.from.y < lowerRight.y && this.to.y > upperLeft.y)
    }

    private fun Point.countVerticalLinesToTheLeft(): Int {
        return polygonLineList.count { pLine ->
            pLine.isVertical && pLine.from.x > this.x && this.y in pLine.from.y..pLine.to.y
        }
    }

}

data class LinePiece private constructor(val from: Point, val to: Point) {
    companion object {
        fun of (from: Point, to: Point): LinePiece {
            assert(from.x == to.x || from.y == to.y)
            val minPoint = if (from.y == to.y) if (from.x < to.x) from else to else if (from.y < to.y) from else to
            val maxPoint = if (from.y == to.y) if (from.x > to.x) from else to else if (from.y > to.y) from else to
            return LinePiece(minPoint,maxPoint)
        }
    }

    val isVertical = (from.x == to.x)
}
