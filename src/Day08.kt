class Digit(val segments: Set<Char>) {
    companion object {
        fun parse(digitStr: String): Digit =
            Digit(digitStr.asSequence().toSet())
    }
}

class SignalPattern(
    private val pattern: List<Digit>,
    private val output: List<Digit>,
) {
    private val bySize = pattern.groupBy { it.segments.size }

    fun getOne(): Set<Char> =
        bySize[2]?.get(0)!!.segments

    fun getFour(): Set<Char> =
        bySize[4]?.get(0)!!.segments

    fun getSeven(): Set<Char> =
        bySize[3]?.get(0)!!.segments

    fun getEight(): Set<Char> =
        bySize[7]?.get(0)!!.segments

    fun determineEG(): Set<Char> =
        (getEight() - (getFour() + getSeven()))

    fun determineA(): Set<Char> =
        (getEight() - (getFour() + determineEG()))

    fun determineBD(): Set<Char> =
        (getEight() - (determineEG() + determineA() + getOne()))

    fun determineBDEG(): Set<Char> =
        getEight() - getSeven()

    private fun findUniqueDigits(): Sequence<Digit> =
        pattern
            .groupBy { it.segments.size }
            .asSequence()
            .filter { (_, v) -> v.size < 2 }
            .map { (_, v) -> v[0] }

    fun countUnique(): Int {
        val uniques = findUniqueDigits().map { it.segments.size }
        return output.filter { uniques.contains(it.segments.size) }.size
    }

    companion object {
        fun parse(line: String): SignalPattern {
            val reading = line.split(" | ")
            return SignalPattern(toDigits(reading[0]), toDigits(reading[1]))
        }

        private fun toDigits(reading: String): List<Digit> =
            reading.split(" ").map { Digit.parse(it) }
    }
}


fun main() {
    fun parse(input: List<String>): List<SignalPattern> =
        input.map { SignalPattern.parse(it) }

    fun part1(input: List<String>): Int {
        return parse(input).sumOf { it.countUnique() }
    }

    fun part2(input: List<String>): Int {
        parse(input).map {
            println(it.determineEG())
            println(it.determineBD())
            println(it.determineA())
            println(it.determineBDEG())
            println(it.getOne())
            println(it.getFour())
            println(it.getSeven())
            println(it.getEight())
            println()
        }
        return TODO()
    }

    val testInput = readInput("Day08_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 26) { "Expected 26, got $testPart1" }
//    val testPart2 = part2(testInput)
//    check(testPart2 == 61229) { "Expected 61229, got $testPart2" }

    val input = readInput("Day08")
    println(part1(input))
//    println(part2(input))
}
