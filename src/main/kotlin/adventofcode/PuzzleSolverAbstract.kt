package adventofcode

import tool.coordinate.twodimensional.pos
import java.io.File

abstract class PuzzleSolverAbstract (
    val test: Boolean,
    private val puzzleName: String = "",
    private val hasInputFile: Boolean = true) {

    private val dayOfMonth = getDayOfMonthFromSubClassName()

    open fun resultPartOne(): Any = "NOT IMPLEMENTED"
    open fun resultPartTwo(): Any = "NOT IMPLEMENTED"

    val inputLines = inputLines()

    fun showResult() {
        val dayNumber = "%2d".format(dayOfMonth)
        println("Day $dayNumber       : $puzzleName")
        println("Version      : ${if (test) "test" else "real"} input")
        println("------------------------------------")

        printResult(1) { resultPartOne().toString() }
        printResult(2) { resultPartTwo().toString() }
        println("==================================================================")
    }

//    fun showResultShort() {
//        println("Day $dayOfMonth (${if (test) "test" else "real"} input) $puzzleName")
//        print("   ")
//        printResult(1) { resultPartOne().toString() }
//        print("   ")
//        printResult(2) { resultPartTwo().toString() }
//        println("==================================================================")
//    }
//
//    fun showResultTimeOnly() {
//        val result = executeOnly()
//        print(" ${result.dayOfMonth.toString().padStart(2, ' ')} ${result.name.padEnd(30, ' ')}: ")
//
//        print("%4d.%03d ms   ".format(result.timePassedPart1Ns / 1_000_000, result.timePassedPart1Ns % 1_000))
//        print("%4d.%03d ms   ".format(result.timePassedPart2Ns / 1_000_000, result.timePassedPart2Ns % 1_000))
//    }
//
    fun executeOnly(): PuzzleResultData{
        val timePassed1 = getResultTimeOnly() { resultPartOne() }
        val timePassed2 = getResultTimeOnly() { resultPartTwo() }
        return PuzzleResultData(dayOfMonth, puzzleName, timePassed1, timePassed2)
    }

    private fun printResult(puzzlePart: Int, getResult: () -> String ) {
        val startTime = System.nanoTime()
        val result = getResult()
        val timePassed = System.nanoTime() - startTime
        print("Result part $puzzlePart: $result (after %d.%03d ms)".format(timePassed / 1_000_000, timePassed % 1_000))
        println()
    }

    private fun getResultTimeOnly(getResult: () -> Any ): Long {
        val startTime = System.nanoTime()
        getResult()
        val timePassed = System.nanoTime() - startTime
        return timePassed
    }


    private fun getDayOfMonthFromSubClassName(): Int {
        val className = this.javaClass.name.lowercase()
        val dayOfMonth = if (className.contains("day")) {
            className.substringAfter("day").take(2)
        } else if (className.contains("december")) {
            className.substringAfter("december").take(2)
        } else {
            className.takeLast(2)
        }
        return dayOfMonth.toInt()
    }

    fun inputLines(testFile: String="example", liveFile: String="input", path:String = defaultPath()) =
        if (test) getInputLines(path, testFile) else getInputLines(path, liveFile)

    private fun defaultPath() = String.format("data/december%02d", dayOfMonth)

    private fun getInputLines(path: String, fileName: String): List<String> {
        val file = File("$path/$fileName")
        val inputLines = if (file.exists()) file.bufferedReader().readLines() else emptyList()
        if (inputLines.isEmpty() && hasInputFile)
            throw Exception("No input lines!!")
        return inputLines
    }

    fun inputAsGrid(testFile: String="example", liveFile: String="input", path:String = defaultPath()) =
        inputLines(testFile=testFile, liveFile=liveFile, path=path)
            .asGrid()

    fun List<String>.asGrid() =
        this
            .flatMapIndexed { y, line ->
                line.mapIndexed { x, ch ->  pos(x,y) to ch}
            }
            .toMap()
}