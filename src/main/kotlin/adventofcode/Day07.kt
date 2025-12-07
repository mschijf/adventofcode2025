package adventofcode

import tool.coordinate.twodimensional.Point

fun main() {
    Day07(test=false).showResult()
}

class Day07(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Laboratories", hasInputFile = true) {

    private val manifoldDiagram = inputLines.asGrid()
    private val start = manifoldDiagram.filter { it.value == 'S' }.keys.first()
    private val splitterSet = manifoldDiagram.filter { it.value == '^' }.keys
    private val manifoldDiagramHeight = manifoldDiagram.keys.maxOf { it.y } + 1

    override fun resultPartOne(): Any {
        var beamsOnCurrentLine = setOf(start)
        var splitCount = 0
        repeat(manifoldDiagramHeight) {
            splitCount += beamsOnCurrentLine.count { beam -> beam.south() in splitterSet}
            beamsOnCurrentLine = beamsOnCurrentLine.goDownOneLine()
        }

        return splitCount
    }

    private fun Set<Point>.goDownOneLine(): Set<Point> {
        return this.flatMap { beam -> beam.goDown() }.toSet()
    }

    private fun Point.goDown(): Set<Point> {
        return if (this.south() in splitterSet) {
            setOf(this.southwest(), this.southeast())
        } else {
            setOf(this.south())
        }
    }

    override fun resultPartTwo(): Any {
        return countPaths(start)
    }

    private val cache = mutableMapOf<Point, Long>()
    private fun countPaths(current:Point) : Long {
        if (current.y >= manifoldDiagramHeight)
            return 1

        if (current in cache)
            return cache[current]!!

        val count = if (current in splitterSet) {
            countPaths(current.east()) + countPaths(current.west())
        } else {
            countPaths(current.south())
        }

        cache[current] = count
        return count
    }
}


