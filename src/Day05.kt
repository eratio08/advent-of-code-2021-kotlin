import kotlin.math.max
import kotlin.math.min

data class Point(val x: Int, val y: Int) {
    companion object {
        fun parse(str: String): Point =
            str.split(",")
                .let { Point(it[0].toInt(), it[1].toInt()) }
    }
}

data class Line(val p1: Point, val p2: Point) {
    val isHorizontal: Boolean = p1.y == p2.y
    val isVertical: Boolean = p1.x == p2.x

    private fun getXRange(): IntRange =
        min(p1.x, p2.x)..max(p1.x, p2.x)

    private fun getYRange(): IntRange =
        min(p1.y, p2.y)..max(p1.y, p2.y)

    private fun linearInterpolatePoints(): List<Point> =
        getXRange().map { Point(it, linearInterpolate(it)) }

    private fun linearInterpolate(x: Int): Int =
        p1.y + ((p2.y - p1.y) / (p2.x - p1.x)) * (x - p1.x)

    fun getPoints(): List<Point> =
        when {
            isVertical -> getYRange().map { Point(p1.x, it) }
            isHorizontal -> getXRange().map { Point(it, p1.y) }
            else -> linearInterpolatePoints()
        }

    companion object {
        fun parse(str: String): Line =
            str.split(" -> ")
                .let { Line(Point.parse(it[0]), Point.parse(it[1])) }
    }
}

data class Plane(val lines: List<Line>) {

    fun getHorizontalVerticalOverlappingPoints(): Int =
        lines.asSequence()
            .filter { it.isVertical || it.isHorizontal }
            .let { getOverlappingPointsSum(it) }

    fun getOverlappingPoints(): Int = getOverlappingPointsSum(lines.asSequence())

    private fun getOverlappingPointsSum(lines: Sequence<Line>): Int =
        lines.flatMap { it.getPoints() }
            .groupBy { it }
            .values
            .filter { it.size > 1 }
            .size

    companion object {
        fun parse(lines: List<String>): Plane =
            Plane(lines.map(Line.Companion::parse))
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return Plane.parse(input).getHorizontalVerticalOverlappingPoints()
    }

    fun part2(input: List<String>): Int {
        return Plane.parse(input).getOverlappingPoints()
    }

    val testInput = readInput("Day05_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 5) { "Expected 5, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 12) { "Expected 12, got $testPart2" }

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
