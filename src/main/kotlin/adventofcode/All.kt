package adventofcode

fun main() {
    val test = false
    val verbose = false

    if (verbose) {
        (1..25).forEach { dayNr -> runDay(dayNr, test=test, true) }
    } else {
        println()
        println("Day Name                                   Init      Puzzle 1      Puzzle 2")
        println("---------------------------------------------------------------------------")
        runDay(1, test=false, false, true)
        (1..25).forEach { dayNr -> runDay(dayNr, test=false, false) }
        println("---------------------------------------------------------------------------")

    }
}

fun runDay(dayNr: Int, test: Boolean, verbose: Boolean, warmingUp: Boolean = false) {
    val className = "Day%02d".format(dayNr)
    val packageName = "adventofcode"
    try {
        val startTime = System.nanoTime()
        val kClass = Class.forName("$packageName.$className").kotlin
        val methodName = if (verbose) "showResultShort" else "executeOnly"
        val method = kClass.members.find { it.name == methodName }
        val obj = kClass.constructors.first().call(test)
        val timePassed0 = System.nanoTime() - startTime
        val response = method!!.call(obj)
        if (!verbose) {
            val result = response as PuzzleResultData
            if (warmingUp) {
                print("    ${"Warming up ...".padEnd(30, ' ')}: ")
                print("%4d.%03d ms   ".format(timePassed0 / 1_000_000, timePassed0 % 1_000))
            } else {
                print(" ${result.dayOfMonth.toString().padStart(2, ' ')} ${result.name.padEnd(30, ' ')}: ")
                print("%4d.%03d ms   ".format(timePassed0 / 1_000_000, timePassed0 % 1_000))
                print("%4d.%03d ms   ".format(result.timePassedPart1Ns / 1_000_000, result.timePassedPart1Ns % 1_000))
                print("%4d.%03d ms   ".format(result.timePassedPart2Ns / 1_000_000, result.timePassedPart2Ns % 1_000))
            }
            println()
        }

    } catch (_: ClassNotFoundException) {
        if (verbose) {
            println("$className not implemented (yet)")
        }
    } catch (otherE: Exception) {
        println("$className runs with exception ${otherE.cause}")
    }
}
