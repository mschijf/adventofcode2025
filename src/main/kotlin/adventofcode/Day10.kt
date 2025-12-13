package adventofcode

import kotlin.collections.joinToString
import kotlin.math.min

fun main() {
    Day10(test=false).showResult()
}

class Day10(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Factory", hasInputFile = true) {

    private val machineList = inputLines.map { Machine.of(it) }

    override fun resultPartOne(): Any {
//        return machineList.sumOf { it.minimalButtonsForLightDiagramBFS() }
        return machineList.sumOf { it.uniqueButtonsForLightDiagram().minOf { it.buttonSet.size } }
    }

    //1017930 too high
    //17971 too high
    //17970 --> correct
    override fun resultPartTwo(): Any {
        return machineList.sumOf { machine -> machine.minimalButtonsForJoltageLevel() }
    }

}

class Machine(
    val lightDiagram: String,
    val buttonList: List<Button>,
    val joltageLevel: List<Int>) {

    private val allLightPatternButtonCombinations = determineAllLightPatternButtonCombinations()

    companion object {
        fun of(raw: String): Machine {
            val lightDiagram = raw.substring(1, raw.indexOf("]")).trim()

            val buttonList = raw
                .substring(raw.indexOf("]")+1, raw.indexOf("{")).trim()
                .split(" ")
                .map {it.substring(1, it.length-1)}
                .map {Button(it.split(",").map{it.trim().toInt()})}

            val joltageLevel = raw
                .substring(raw.indexOf("{")+1, raw.indexOf("}")).trim()
                .split(",").map{it.trim().toInt()}
            return Machine(lightDiagram, buttonList, joltageLevel)
        }
    }

    // first we create with the methode 'powerSet()' all button combinations possible, so that we have only combinations
    // with unique buttons (including a set with no buttons at all).
    // For each combination of buttons we determine the light pattern that arises when all buttons of the combination are pressed

    private fun determineAllLightPatternButtonCombinations(): Map<String, List<ButtonCombination>> {
        val lightsOff = ".".repeat(lightDiagram.length)
        return buttonList
            .powerSet()
            .map{ ButtonCombination(it) }
            .groupBy { lightsOff.pressAllButtons(it) }
    }

    //
    // first solution for part 1, still works
    //
    fun minimalButtonsForLightDiagramBFS(): Int {
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<Pair<String, Int>>()
        val allLightsOff = ".".repeat(lightDiagram.length)
        queue.add(Pair(lightDiagram, 0))
        while (queue.isNotEmpty()) {
            val (current, count) = queue.removeFirst()
            visited.add(current)
            if (current == allLightsOff)
                return count
            buttonList.forEach { button ->
                val next = current.pressButton(button)
                if (next !in visited)
                    queue.add(Pair(next, count+1))
            }
        }
        return -1
    }

    fun uniqueButtonsForLightDiagram(current: String = lightDiagram): List<ButtonCombination> {
        return allLightPatternButtonCombinations[current] ?:emptyList()
    }

    // part2
    // the first approaches did not work (see commented out algorithm at bottom of this file)
    // I think they are correct ( I verified them for a few), but it took way too long for all input examples
    //
    // After that I did it via a different way:
    //  -  suppose the joltage level = {4, 8, 12, 36}
    //  - then the minimum number of buttons (f) that we need to press is :
    //      f(4, 8, 12, 36) = f(2, 4, 6, 18) + f(2, 4, 6, 18)
    //                      = 2 * f(2, 4, 6, 18)
    //    but this only works when all joltage levels are even. What to do when one or more are odd?
    //    then we have to press some buttons so, that after pressing these buttons, we have an odd number again.
    //    But if we see and odd number as 'light on' and an even number as 'light off', then we can use the code
    //    that gives the button combinations that produces this lightPattern.
    //
    //    Suppose we have f(3,4,5,7). this corresponds to light "#.##" and for this pattern, we have several button
    //    combinations that brings it to "....". Those are stored in uniqueButtonsForLightDiagram
    //    For each button combination we do the following:
    //    - we 'execute' the buttonCombination (press all buttons on current joltage level)
    //    - if we would do that on the corresponding lightlevel, then we would have turned off all lights
    //    - for joltage, it means that all values are even (or 0, or negative...)
    //
    //    in other words, suppose we have a combination, existing of 3 buttons, that turns {3,4,5,7} after pressing into {2,2,4,6}
    //    now we can do the trick again: f(3,4,5,7) = 3 + 2 * f(2/2, 2/2, 4/2, 6/2)
    //                                              = 3 + 2 * f(1, 1, 2, 3)
    //    doing this for all combinations that turns {3,4,5,7} after pressing into a joltage Level with only even numbers
    //    and calculating the minimum of all these combinations, we can determin the minimum numbers used.
    //
    //    this recursive function leads to the code below

    fun minimalButtonsForJoltageLevel(currentJoltageLevel: List<Int> = joltageLevel): Int {
        if (currentJoltageLevel.all { it == 0 })
            return 0

        val buttonCombiSet = uniqueButtonsForLightDiagram( currentJoltageLevel.joltageLevelToLightDiagram() )

        var min = 1_000_000 //not Int.MAX_VALUE --> can lead to a negative number, when it is multiplied
        buttonCombiSet.forEach { buttonCombination ->
            val nextJoltageLevel = currentJoltageLevel.pressAllButtons(buttonCombination)
            if (nextJoltageLevel.all{ it >= 0}) {
                min = min(
                    min,
                    buttonCombination.buttonSet.size + 2 * minimalButtonsForJoltageLevel(nextJoltageLevel.div2())
                )
            }
        }
        return min
    }

    private fun List<Int>.joltageLevelToLightDiagram(): String {
        return this.joinToString("") { digit -> if (digit % 2 == 1) "#" else "." }
    }

    private fun String.pressButton(button: Button): String {
        return this.mapIndexed { idx, ch ->
            if (idx in button.indexList)
                if (ch == '.') '#' else '.'
            else
                ch
        }.joinToString("")
    }

    private fun List<Int>.div2(): List<Int> {
        return this.map {it / 2 }
    }

    private fun List<Int>.pressAllButtons(buttonCombination: ButtonCombination): List<Int> {
        var tmp = this
        buttonCombination.buttonSet.forEach { button ->
            tmp = tmp.pressButton(button)
        }
        return tmp
    }

    private fun String.pressAllButtons(buttonCombination: ButtonCombination): String {
        var tmp = this
        buttonCombination.buttonSet.forEach { button ->
            tmp = tmp.pressButton(button)
        }
        return tmp
    }

    private fun List<Int>.pressButton(button: Button): List<Int> {
        return this.mapIndexed { idx, lvl ->
            if (idx in button.indexList)
                lvl - 1
            else
                lvl
        }
    }
}

data class Button(val indexList: List<Int>)
data class ButtonCombination(val buttonSet: Set<Button>) {
    fun contains(button: Button) = button in buttonSet
    fun plus(button: Button) = ButtonCombination(buttonSet + button)
}

fun <T> List<T>.powerSet(): List<Set<T>> =
    fold(listOf(emptySet())) { acc, e ->
        acc + acc.map { it + e }
    }



//===============================================================================================================


//
// First approaches for part2
//

//var bestSoFar = Int.MAX_VALUE
//val cache = mutableMapOf<Pair<List<Int>, Set<Button>>, Int>()
//fun minimalButtonsForJoltageLevel(currentLevel: List<Int> = joltageLevel, stepsDone: Int = 0, forbiddenButtons: Set<Button> = emptySet()): Int {
//    if (stepsDone >= bestSoFar)
//        return Int.MAX_VALUE
//
//    if (currentLevel.all{ it == 0 }) {
//        return 0
//    }
//    val key = Pair(currentLevel, forbiddenButtons)
//    if (cache.contains(key))
//        return cache[key]!!
//
//    var best = Int.MAX_VALUE
//    var bestUsages = 0
//    val (minJoltageIndex, minJoltage) = currentLevel.smallestNotZero()
//    val zeroIndexesSet = currentLevel.zeroIndexes()
//    val candidateButtons = buttonList
//        .filter { it.indexList.contains( minJoltageIndex ) && it.indexList.none { idx -> idx in zeroIndexesSet }}
//        .filter { it !in forbiddenButtons }
//
//    candidateButtons.forEachIndexed { idx, button ->
//        for (usages in minJoltage downTo 0) {
//            val x = this@Machine.minimalButtonsForJoltageLevel(
//                currentLevel.pressButton(usages, button),
//                stepsDone + usages,
//                forbiddenButtons + candidateButtons.subList(0, idx+1).toSet()
//            )
//            if (x < best) {
//                best = x
//                bestUsages = usages
//            }
//        }
//    }
//    cache[key] = best + bestUsages
//    return best + bestUsages
//}
//
//private fun List<Int>.smallestNotZero() : Pair<Int, Int> {
//    var smallestIndex = -1
//    var smallest = Int.MAX_VALUE
//    this.forEachIndexed { idx, v ->
//        if (v != 0 && v < smallest) {
//            smallest = v
//            smallestIndex = idx
//        }
//    }
//    return Pair(smallestIndex, smallest)
//}
//
//private fun List<Int>.indexOfLargest() : Int {
//    var largestIndex = -1
//    var largest = Int.MIN_VALUE
//    this.forEachIndexed { idx, v ->
//        if (v != 0 && v > largest) {
//            largest = v
//            largestIndex = idx
//        }
//    }
//    return largestIndex
//}
//
//
//private fun List<Int>.zeroIndexes() : Set<Int> {
//    val result = mutableSetOf<Int>()
//    this.forEachIndexed { idx, v ->
//        if (v == 0)
//            result.add(idx)
//    }
//    return result
//}
//
//private fun List<Int>.pressButton(usages: Int, button: Button): List<Int> {
//    return this.mapIndexed { idx, lvl ->
//        if (idx in button.indexList)
//            lvl - usages
//        else
//            lvl
//    }
//}

