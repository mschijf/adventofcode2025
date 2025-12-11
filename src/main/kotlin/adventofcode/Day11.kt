package adventofcode

fun main() {
    Day11(test=false).showResult()
}

class Day11(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Reactor", hasInputFile = true) {


    override fun resultPartOne(): Any {
        val deviceMap = inputLines.map { Device.of(it) }.associateBy { it.name }
        return deviceMap.countPaths("you", finish = "out")
    }

    override fun resultPartTwo(): Any {
        val deviceMap = inputLines(testFile = "example2").map { Device.of(it) }.associateBy { it.name }

        //from svr -> ... --> dac --> ... --> fft --> ... --> out
        val step1 = deviceMap.countPaths("svr", finish = "fft", ignore=setOf("dac", "out") )
        val step2 = deviceMap.countPaths("fft", finish = "dac", ignore=setOf("out"))
        val step3 = deviceMap.countPaths("dac", finish = "out", ignore=setOf("fft"))

        //from svr -> ... --> fft --> ... --> dac --> ... --> out
        val step4 = deviceMap.countPaths("svr", finish = "dac", ignore=setOf("fft", "out"))
        val step5 = deviceMap.countPaths("dac", finish = "fft", ignore=setOf("out"))
        val step6 = deviceMap.countPaths("fft", finish = "out", ignore=setOf("dac"))

        return step1*step2*step3 + step4*step5*step6
    }

    private fun Map<String, Device>.countPaths(current: String, finish : String, ignore:Set<String> = emptySet(),
                                               cache: MutableMap<String, Long> = mutableMapOf<String, Long> ()): Long {
        if (current == finish)
            return 1

        if (cache.contains(current))
            return cache[current]!!

        val sum = this[current]!!.attached.filter { it !in ignore }.sumOf { deviceName ->
            countPaths(deviceName, finish, ignore, cache)
        }
        cache[current] = sum
        return sum
    }
}

data class Device(
    val name: String,
    val attached: List<String>
) {
    companion object {
        fun of(raw: String): Device {
            //ccc: ddd eee fff
            val name = raw.substringBefore(":")
            val attached = raw.substringAfter(":").trim().split(" ")
            return Device(name, attached)
        }
    }
}
