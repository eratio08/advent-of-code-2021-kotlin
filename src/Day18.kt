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

    fun buildTrajectory(p: Probe, ta: TargetArea): List<Probe> {
        val trajectory = generateSequence(p.step()) { prev ->
            val next = prev.step()
            if (next.isAfter(ta)) null
            else next
        }.toList()
        return if (trajectory.any { it.hits(ta) }) {
            trajectory
        } else {
            emptyList()
        }
    }

    fun optimizeForHeight(ta: TargetArea): Probe {
        var x = 6
        var y = 9
        var highestY = 0
        (0 until 1000).forEach {
            val probe = Probe(x, y)
            val tra = buildTrajectory(probe, ta).toList()
            val hitsTarget = tra.any {
                val c = it.hits(ta)
                c
            }
            if (tra.isEmpty() || !hitsTarget) {
                y += 1
            } else {
                val maxY = tra.maxOf { it.y }
                if (maxY >= highestY) {
                    highestY = maxY
                    y += 1
                } else {
                    x += 1
                }

            }
        }
        TODO()
    }

    fun part1(input: List<String>): Int {
        val ta = parse(input.first())
        val x = buildTrajectory(Probe(6, 9), ta).toList()
        val maxH = x.firstOrNull { it.yVelocity == 0 }?.y
//        val n = optimizeForHeight(ta)
        TODO()
    }

    fun part2(input: List<String>): Long {
        TODO()
    }

    val testInput = readInput("Day17_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 45) { "Expected 45, got $testPart1" }
//    val testPart2 = part2(testInput)
//    check(testPart2 == 1L) { "Expected 1, got $testPart2" }

    val input = readInput("Day17")
    println(part1(input))
//    println(part2(input))
}
