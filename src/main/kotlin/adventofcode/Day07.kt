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
        var beamsOnCurrentLine = setOf<Point>(start)
        val totalBeams = beamsOnCurrentLine.toMutableSet()

        repeat(manifoldDiagramHeight) {
            beamsOnCurrentLine = beamsOnCurrentLine.flatMap { beam -> beam.goDown() }.toSet()
            totalBeams += beamsOnCurrentLine
        }

        return totalBeams.map { beam -> beam.south() }.toSet().intersect(splitterSet).count()
    }

    private fun Point.goDown(): Set<Point> {
        val newPlace = this.south()
        return if (newPlace in splitterSet) {
            setOf(newPlace.west(), newPlace.east())
        } else {
            setOf(newPlace)
        }
    }

    override fun resultPartTwo(): Any {
        return countPaths(start)
    }

    val cache = mutableMapOf<Point, Long>()
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


