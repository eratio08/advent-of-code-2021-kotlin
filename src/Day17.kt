fun main() {
    data class TargetArea(val xs: IntRange, val ys: IntRange) {
        val xMax = xs.maxOf { it }
        val yMin = ys.minOf { it }
    }

    data class Probe(val xVelocity: Int, val yVelocity: Int, val x: Int = 0, val y: Int = 0) {
        fun step(): Probe {
            val newXVelocity = when {
                xVelocity > 0 -> xVelocity - 1
                xVelocity < 0 -> xVelocity + 1
                else -> 0
            }
            return copy(x = x + xVelocity, y = y + yVelocity, xVelocity = newXVelocity, yVelocity = yVelocity - 1)
        }

        fun hits(ta: TargetArea): Boolean =
            ta.xs.contains(x) && ta.ys.contains(y)

        fun isAfter(ta: TargetArea): Boolean =
            x > ta.xMax || y < ta.yMin
    }

    fun parse(line: String): TargetArea {
        val ranges = line.substringAfter("target area: ").split(", ")
        val xsParts = ranges[0].substringAfter("x=").split("..")
        val xs = (xsParts[0].toInt()..xsParts[1].toInt())
        val ysParts = ranges[1].substringAfter("y=").split("..")
        val ys = (ysParts[0].toInt()..ysParts[1].toInt())
        return TargetArea(xs, ys)
    }

    fun buildTrajectory(p: Probe, ta: TargetArea): Sequence<Probe> {
        return generateSequence(p) { prev -> prev.step() }
            .takeWhile { !it.isAfter(ta) }
    }

    fun findVxs(vy0: Int, ta: TargetArea): Sequence<Int> {
        var wasHitBefore = false
        return (0..ta.xMax).asSequence()
            .map {
                val isHitting = buildTrajectory(Probe(it, vy0), ta).any { probe -> probe.hits(ta) }
                it to isHitting
            }
            .takeWhile { (_, isHitting) ->
                if (isHitting) {
                    wasHitBefore = true
                }
                (!isHitting && !wasHitBefore) || isHitting
            }
            .filter { (_, isHitting) -> isHitting }
            .map { (vx, _) -> vx }
    }

    fun part1(input: List<String>): Int {
        val ta = parse(input.first())
        val y = (ta.yMin..ta.xMax).asSequence()
            .map { vy -> vy to findVxs(vy, ta) }
            .mapNotNull { (vy, vxs) ->
                vxs.mapNotNull { buildTrajectory(Probe(it, vy), ta).firstOrNull { p -> p.yVelocity == 0 } }
                    .maxByOrNull { p -> p.y }
            }.fold(0) { yMax, p -> if (p.y > yMax) p.y else yMax }
        return y
    }

    fun part2(input: List<String>): Int {
        val ta = parse(input.first())
        val initials = (ta.yMin..ta.xMax).asSequence()
            .flatMap { vy -> findVxs(vy, ta).asSequence().map { vx -> vx to vy } }
            .toSet()
        return initials.size
    }

    val testInput = readInput("Day17_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 45) { "Expected 45, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 112) { "Expected 112, got $testPart2" }

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}
