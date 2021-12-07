import kotlin.math.abs

fun determineAvgDistance(crabs: List<Int>, position: Int): Int =
    crabs.sumOf { abs(it - position) }

fun gaussianSum(n: Int): Int =
    n * (n + 1) / 2

fun determineAvgDistance2(crabs: List<Int>, position: Int): Int =
    crabs.sumOf { gaussianSum(abs(it - position)) }

fun determineAlignmentPosition(crabs: List<Int>, distanceDeterminer: (List<Int>, Int) -> Int): Pair<Int, Int> =
    crabs.indices.asSequence()
        .map { it to distanceDeterminer(crabs, it) }
        .sortedBy { it.second }
        .first()

fun main() {
    fun part1(input: List<String>): Int {
        return input.asSequence()
            .flatMap { it.split(",") }
            .map { it.toInt() }
            .toList()
            .let { determineAlignmentPosition(it, ::determineAvgDistance).second }
    }

    fun part2(input: List<String>): Int {
        return input.asSequence()
            .flatMap { it.split(",") }
            .map { it.toInt() }
            .toList()
            .let { determineAlignmentPosition(it, ::determineAvgDistance2).second }
    }

    val testInput = readInput("Day07_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 37) { "Expected 37, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 168) { "Expected 168, got $testPart2" }

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
