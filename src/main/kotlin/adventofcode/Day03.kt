package adventofcode

fun main() {
    Day03(test=false).showResult()
}

class Day03(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Lobby", hasInputFile = true) {

    private val batteryPackList = inputLines.map{ it.map { ch -> ch - '0' }}
    override fun resultPartOne(): Any {
        return batteryPackList.sumOf{ it.maxJoltage() }
    }

    private fun List<Int>.maxJoltage() : Int {
        val firstMax = this.dropLast(1).max()
        val index = this.indexOf (firstMax )
        val secondMax = this.drop(index+1).max()
        return 10*firstMax + secondMax
    }

    override fun resultPartTwo(): Any {
        return batteryPackList.sumOf{ it.maxJoltage12() }
    }

    private fun List<Int>.maxJoltage12() : Long {
        var list = this
        var total = 0L
        for (i in 12 downTo 1) {
            val nextMax = list.dropLast(i-1).max()
            val index = list.indexOf(nextMax)
            list = list.drop(index+1)
            total = total * 10 + nextMax
        }
        println(total)
        return total
    }
//169709990062889
}


