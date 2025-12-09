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
    Day09(test=true).showResult()
}

class Day09(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val coordinateList = inputLines.map { pos(it) }

    override fun resultPartOne(): Any {
        return coordinateList.mapCombinedItems { first, second -> rectangleArea(first, second) }.max()
    }

    //1398441360 --> too low
    override fun resultPartTwo(): Any {
        val polygon = Polygon(coordinateList)

        return(polygon.containsRectangle(pos(9,5), pos(2,3)))

        return coordinateList
            .filterCombinedItems { first, second -> polygon.containsRectangle(first, second) }
            .maxBy { pair -> rectangleArea(pair.first, pair.second) }
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

//        println("------------")
//        println(xCoordinateGroup.values.filter { it.size != 2 })
//        println(yCoordinateGroup.values.filter { it.size != 2 })
//        println("------------")

        val verticalLines = xCoordinateGroup.values.flatMap { value -> value.sortedBy{ it.y }.chunked(2).map { LinePiece.of(it[0], it[1]) }}
        val horizontalLines = yCoordinateGroup.values.flatMap { value -> value.sortedBy{ it.x }.chunked(2).map { LinePiece.of(it[0], it[1]) }}
//        val verticalLines = xCoordinateGroup.values.map { LinePiece.of(it.first(), it.last()) }
//        val horizontalLines = yCoordinateGroup.values.filter { it.size == 2}.map { LinePiece.of(it.first(), it.last()) }

        return verticalLines + horizontalLines
    }

    fun containsRectangle(first: Point, second: Point): Boolean {
        val u0 = pos(min(first.x, second.x), min(first.y, second.y) )
        val u1 = pos(max(first.x, second.x), min(first.y, second.y))
        val d0 = pos(min(first.x, second.x), max(first.y, second.y) )
        val d1 = pos(max(first.x, second.x), max(first.y, second.y))

        val upperLine = LinePiece.of(u0, u1)
        val leftLine = LinePiece.of(u0, d0)
        val rightLine = LinePiece.of(u1, d1)
        val bottomLine = LinePiece.of(d0, d1)

        val u = upperLine.crossesPolygonLineList()
        val l = leftLine.crossesPolygonLineList()
        val r = rightLine.crossesPolygonLineList()
        val b = bottomLine.crossesPolygonLineList()

        if (u || l || r || b)
            return false
        return true

        if (upperLine.countLinesBelow() % 2 == 0)
            return false
        if (bottomLine.countLinesBelow() % 2 == 0)
            return false

        if (leftLine.countLinesRight() % 2 == 0)
            return false
        if (rightLine.countLinesRight() % 2 == 0)
            return false

        return true

        return !(u || l || r || b)

        return ! (
                upperLine.crossesPolygonLineList() ||
                leftLine.crossesPolygonLineList() ||
                rightLine.crossesPolygonLineList() ||
                bottomLine.crossesPolygonLineList()
                )
    }

    private fun LinePiece.crossesPolygonLineList(): Boolean {
        return polygonLineList.any { pl -> this.crosses(pl) }
    }

    private val horLines = polygonLineList.filter { it.isHorizontal }
    private val verLines = polygonLineList.filter { it.isVertical }

    private fun LinePiece.countLinesBelow(): Int {
        return horLines.count { it.from.y >= this.from.y && this.from.x in (it.from.x .. it.to.x) }
    }
    private fun LinePiece.countLinesRight(): Int {
        return verLines.count { it.from.x >= this.from.x && this.from.y in (it.from.y .. it.to.y) }
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

    val isHorizontal = from.y == to.y
    val isVertical = from.x == to.x

    fun crosses(otherLine: LinePiece): Boolean {
        if (this.isHorizontal && otherLine.isHorizontal) {
            if (this.from.y != otherLine.from.y) //parallel
                return false
            if (this.from.x >= otherLine.from.x && this.to.x <= otherLine.to.x) //this ligt in otherLine
                return false
            if (this.to.x <= otherLine.from.x || this.from.x >= otherLine.to.x) //this ligt helemaal links of helemaal rechts van otherline
                return false
            return true //gedeeltelijk overlappend
        }

        if (this.isVertical && otherLine.isVertical) {
            if (this.from.x != otherLine.from.x) //parallel
                return false
            if (this.from.y >= otherLine.from.y && this.to.y <= otherLine.to.y) //this ligt in otherLine
                return false
            if (this.to.y <= otherLine.from.y || this.from.y >= otherLine.to.y) //this ligt helemaal boven of helemaal onder otherline
                return false
            return true //gedeeltelijk overlappend
        }

        if (this.isHorizontal) {
            if (this.from.x < otherLine.from.x && this.to.x > otherLine.from.x && otherLine.from.y < this.from.y && otherLine.to.y > this.from.y)
                return true
            return false
        }

        if (this.isVertical) {
            if (this.from.y < otherLine.from.y && this.to.y > otherLine.from.y && otherLine.from.x < this.from.x && otherLine.to.x > this.from.x)
                return true
            return false
        }

        return false
    }
}

//private fun countFrom(fromPoint: Point): Int {
//    val visited = mutableSetOf<Point>()
//    var count = 0
//    val queue = ArrayDeque<Point>()
//    queue.add(fromPoint)
//    while (queue.isNotEmpty()) {
//        val current = queue.removeFirst()
//        visited += current
//        //do somethimg with current
//        // add new nodes arrived from currnet to queue
//        queue.addAll(
//            current
//                .neighbors()
//                .filter { nb -> nb !in visited}
//        )
//    }
//    return count
//}
//


