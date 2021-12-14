import kotlin.math.abs

fun main() {

    data class Dot(val x: Int, val y: Int)

    data class FoldInstruction(val axis: String, val value: Int)

    fun parse(input: List<String>): Pair<Set<Dot>, List<FoldInstruction>> {
        return input.fold(mutableSetOf<Dot>() to mutableListOf<FoldInstruction>()) { (points, instructions), line ->
            if (line == "") {
                // skip this lines
            } else if (line.startsWith("fold along ")) {
                val parts = line.substringAfter("fold along ").split("=")
                instructions.add(FoldInstruction(parts[0], parts[1].toInt()))
            } else {
                val parts = line.split(",")
                points.add(Dot(parts[0].toInt(), parts[1].toInt()))
            }
            points to instructions
        }
    }

    fun mirrorAlong(value: Int, axis: Int): Int =
        abs(value - (2 * axis))

    fun mirrorAlongIfBeyondFold(axis: Int, getter: () -> Int): Int {
        val value = getter()
        return if (value < axis) {
            value
        } else {
            mirrorAlong(value, axis)
        }
    }

    fun foldUp(yLine: Int, dots: Set<Dot>): Set<Dot> {
        return dots.asSequence()
            .map { dot ->
                require(dot.y != yLine) { "Dot $dot is on the y line $yLine" }
                dot.copy(y = mirrorAlongIfBeyondFold(yLine) { dot.y })
            }
            .toSet()
    }

    fun foldLeft(xLine: Int, dots: Set<Dot>): Set<Dot> {
        return dots.asSequence()
            .map { dot ->
                require(dot.x != xLine) { "Dot $dot is on the x line $xLine" }
                dot.copy(x = mirrorAlongIfBeyondFold(xLine) { dot.x })
            }
            .toSet()
    }

    fun part1(input: List<String>): Int {
        val (dots, instructions) = parse(input)

        return instructions.take(1).fold(dots) { dots_, instruction ->
            when (instruction.axis) {
                "x" -> foldLeft(instruction.value, dots_)
                "y" -> foldUp(instruction.value, dots_)
                else -> throw IllegalArgumentException("Unknown axis ${instruction.axis}")
            }
        }.size
    }

    fun part2(input: List<String>): Int {
        val (dots, instructions) = parse(input)
        val dots_ = instructions.fold(dots) { dots_, instruction ->
            when (instruction.axis) {
                "x" -> foldLeft(instruction.value, dots_)
                "y" -> foldUp(instruction.value, dots_)
                else -> throw IllegalArgumentException("Unknown axis ${instruction.axis}")
            }
        }
        val height = dots_.asSequence().map { it.y }.sorted().last()
        val width = dots_.asSequence().map { it.x }.sorted().last()
        val canvas = (0..height).map { (0..width).map { "." }.toMutableList() }
        dots_.forEach { (x, y) -> canvas[y][x] = "#" }
        println(canvas.joinToString("\n") { it.joinToString("") { it.padStart(2) } })
        return 16
    }

    val testInput = readInput("Day13_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 17) { "Expected 17, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 16) { "Expected 16, got $testPart2" }

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
