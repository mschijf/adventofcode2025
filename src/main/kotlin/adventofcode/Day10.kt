package adventofcode

fun main() {
    Day10(test=false).showResult()
}

class Day10(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Factory", hasInputFile = true) {

    private val machineList = inputLines.map { Machine.of(it)}

    override fun resultPartOne(): Any {
        return machineList.sumOf { it.minimalButtonsForLightDiagram() }
    }

    override fun resultPartTwo(): Any {
        return "NOT WORKING YET"

//        return machineList[25].minimalButtonsForJoltageLevel()
        var i = 0
        return machineList.sumOf {
            val x = it.minimalButtonsForJoltageLevel()
            i++
            println("$i: $x")
            x
        }
    }
}

class Machine(
    val lightDiagram: String,
    val buttonList: List<List<Int>>,
    val joltageLevel: List<Int>) {

    companion object {
        fun of(raw: String): Machine {
            //[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
            val lightDiagram = raw.substring(1, raw.indexOf("]")).trim()

            val buttonList = raw
                .substring(raw.indexOf("]")+1, raw.indexOf("{")).trim()
                .split(" ")
                .map {it.substring(1, it.length-1)}
                .map {it.split(",").map{it.trim().toInt()}}

            val joltageLevel = raw
                .substring(raw.indexOf("{")+1, raw.indexOf("}")).trim()
                .split(",").map{it.trim().toInt()}
            return Machine(lightDiagram, buttonList, joltageLevel)
        }
    }

    fun minimalButtonsForLightDiagram(): Int {
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<Pair<String, Int>>()
        val startDiagram = ".".repeat(lightDiagram.length)
        queue.add(Pair(startDiagram, 0))
        while (queue.isNotEmpty()) {
            val (current, count) = queue.removeFirst()
            visited.add(current)
            if (current == lightDiagram)
                return count
            buttonList.forEach { button ->
                val next = current.pressButton(button)
                if (next !in visited)
                    queue.add(Pair(next, count+1))
            }
        }
        return -1
    }

    private fun String.pressButton(button: List<Int>): String {
        return this.mapIndexed { idx, ch ->
            if (idx in button)
                if (ch == '.') '#' else '.'
            else
                ch
        }.joinToString("")
    }

    var bestSoFar = Int.MAX_VALUE
    val cache = mutableMapOf<Pair<List<Int>, Set<List<Int>>>, Int>()
    fun minimalButtonsForJoltageLevel(currentLevel: List<Int> = joltageLevel, stepsDone: Int = 0, forbiddenButtons: Set<List<Int>> = emptySet<List<Int>>()): Int {
        if (stepsDone >= bestSoFar)
            return Int.MAX_VALUE

        if (currentLevel.all{ it == 0 }) {
            return 0
        }
        val key = Pair(currentLevel, forbiddenButtons)
        if (cache.contains(key))
            return cache[key]!!

        var best = Int.MAX_VALUE
        var bestUsages = 0
        val (minJoltageIndex, minJoltage) = currentLevel.smallestNotZero()
        val zeroIndexesSet = currentLevel.zeroIndexes()
        val candidateButtons = buttonList
            .filter { it.contains( minJoltageIndex ) && it.none { idx -> idx in zeroIndexesSet }}
            .filter { it !in forbiddenButtons }

        candidateButtons.forEachIndexed { idx, button ->
                for (usages in minJoltage downTo 0) {
                    val x = minimalButtonsForJoltageLevel(
                        currentLevel.pressButton(usages, button),
                        stepsDone + usages,
                        forbiddenButtons + candidateButtons.subList(0, idx+1).toSet()
                    )
                    if (x < best) {
                        best = x
                        bestUsages = usages
                    }
                }
            }
        cache[key] = best + bestUsages
        return best + bestUsages
    }

    private fun List<Int>.smallestNotZero() : Pair<Int, Int> {
        var smallestIndex = -1
        var smallest = Int.MAX_VALUE
        this.forEachIndexed { idx, v ->
            if (v != 0 && v < smallest) {
                smallest = v
                smallestIndex = idx
            }
        }
        return Pair(smallestIndex, smallest)
    }

    private fun List<Int>.indexOfLargest() : Int {
        var largestIndex = -1
        var largest = Int.MIN_VALUE
        this.forEachIndexed { idx, v ->
            if (v != 0 && v > largest) {
                largest = v
                largestIndex = idx
            }
        }
        return largestIndex
    }


    private fun List<Int>.zeroIndexes() : Set<Int> {
        val result = mutableSetOf<Int>()
        this.forEachIndexed { idx, v ->
            if (v == 0)
                result.add(idx)
        }
        return result
    }


    private fun List<Int>.pressButton(usages: Int, button: List<Int>): List<Int> {
        return this.mapIndexed { idx, lvl ->
            if (idx in button)
                lvl-usages
            else
                lvl
        }
    }
}


