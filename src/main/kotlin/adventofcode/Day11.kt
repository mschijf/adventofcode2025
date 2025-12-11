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
        val step1 = deviceMap.countPaths("svr", finish = "fft", ignore="dac")
        val step2 = deviceMap.countPaths("fft", finish = "dac", ignore="")
        val step3 = deviceMap.countPaths("dac", finish = "out", ignore="")
//        println ( "$step1 - $step2 - $step3")
        val step4 = deviceMap.countPaths("svr", finish = "dac", ignore="fft")
        val step5 = deviceMap.countPaths("dac", finish = "fft", ignore="")
        val step6 = deviceMap.countPaths("fft", finish = "out", ignore="")
//        println ( "$step4 - $step5 - $step6")
        return step1*step2*step3 + step4*step5*step6
    }


    //91840542123453266 too high
    //349322478796032
    //10606071 too low

    private fun Map<String, Device>.countPaths(current: String, finish : String, ignore:String = "",
                                               cache: MutableMap<String, Long> = mutableMapOf<String, Long> ()): Long {
        if (finish != "out" && current == "out")
            return 0

        if (current == finish) {
            return 1
        }

        val key = current
        if (cache.contains(key)) {
            return cache[key]!!
        }

        var sum = 0L
        this[current]!!.attached.filter { it != ignore }.forEach { deviceName ->
            val x = countPaths(deviceName, finish, ignore, cache)
            sum += x
        }
        cache[key] = sum
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
