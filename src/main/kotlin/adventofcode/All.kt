package adventofcode

fun main() {
    val test = false
    val executeLevel = ExecuteLevel.TIMEONLY

    if (executeLevel == ExecuteLevel.VERBOSE) {
        (1..25).forEach { dayNr -> runDay(dayNr, test=test, executeLevel) }
    } else {
        runDay(0, test=false, executeLevel)
        println("Day Name                            Puzzle 1   Puzzle 2   Init    ")
        println("-------------------------------------------------------------------")
        (1..25).forEach { dayNr -> runDay(dayNr, test=false, executeLevel) }
        println("-------------------------------------------------------------------")
    }
}

fun runDay(dayNr: Int, test: Boolean, executeLevel: ExecuteLevel) {
    val className = "Day%02d".format(dayNr)
    val packageName = "adventofcode"
    try {
        val startTime = System.currentTimeMillis()
        val kClass = Class.forName("$packageName.$className").kotlin
        val methodName = when (executeLevel) {
            ExecuteLevel.VERBOSE -> "showResultShort"
            ExecuteLevel.TIMEONLY -> "showResultTimeOnly"
            ExecuteLevel.EXECUTEONLY -> "onlyExecute"
        }
        val method = kClass.members.find { it.name == methodName }
        val obj = kClass.constructors.first().call(test)
        val timePassed = System.currentTimeMillis() - startTime
        method!!.call(obj)
        print("%d.%03d sec  ".format(timePassed / 1000, timePassed % 1000))
        println()

    } catch (_: ClassNotFoundException) {
        if (executeLevel == ExecuteLevel.VERBOSE) {
            println("$className not implemented (yet)")
        }
    } catch (otherE: Exception) {
        println("$className runs with exception ${otherE.cause}")
    }
}

enum class ExecuteLevel {
    VERBOSE, TIMEONLY, EXECUTEONLY
}