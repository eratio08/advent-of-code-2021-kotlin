data class Cave(val spots: MutableList<MutableList<Int>>) {
    private val width = spots.first().size
    private val height = spots.size

    private fun isInBounds(x: Int, y: Int): Boolean =
        x in 0 until width && (y in 0 until height)

    private fun generateSurrounding(x: Int, y: Int): Sequence<Pair<Int, Int>> =
        directions.asSequence()
            .map { (dx, dy) -> x + dx to y + dy }
            .filter { (dx, dy) -> isInBounds(dx, dy) }

    private fun findLowestPoints(): Sequence<Triple<Int, Int, Int>> =
        (0 until height).asSequence().flatMap { y ->
            val row = (0 until width).asSequence().mapNotNull { x ->
                val elevation = spots[y][x]
                val isAnyLower = generateSurrounding(x, y)
                    .map { (xx, yy) -> spots[yy][xx] }
                    .any { it <= elevation }
                if (!isAnyLower) Triple(x, y, elevation) else null
            }
            row
        }

    fun calculateRiscFactorSum(): Int =
        findLowestPoints().fold(0) { acc, (_, _, elevation) -> acc + elevation + 1 }

    private fun findSurroundingBasin(
        x: Int,
        y: Int,
    ): List<Triple<Int, Int, Int>> {
        val elevation = spots[y][x]
        spots[y][x] = 9
        val surrounding = generateSurrounding(x, y)
            .map { (dx, dy) -> Triple(dx, dy, spots[dy][dx]) }
            .filter { (_, _, elev) -> elev < 9 }
            .toList()
        return if (surrounding.isEmpty()) {
            listOf()
        } else {
            surrounding + listOf(Triple(x, y, elevation)) + surrounding.asSequence()
                .flatMap { (x, y) -> findSurroundingBasin(x, y).asSequence() }.toList()
        }
    }

    private fun walkSurroundingBasin(spot: Triple<Int, Int, Int>): Set<Triple<Int, Int, Int>> =
        findSurroundingBasin(spot.first, spot.second).toSet()

    fun calculateLargesBasinsProduct(): Int {
        val basins = findLowestPoints()
            .map { spot -> walkSurroundingBasin(spot).size }
            .sortedDescending()
            .toList()
        return basins.take(3).fold(1) { acc, i -> acc * i }
    }

    companion object {
        private val directions = listOf(-1 to 0, 0 to -1, 1 to 0, 0 to 1)
        fun parse(lines: List<String>): Cave =
            Cave(lines.asSequence()
                .map { it.asSequence().map { it.toString().toInt() }.toMutableList() }
                .toMutableList())
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return Cave.parse(input).calculateRiscFactorSum()
    }

    fun part2(input: List<String>): Int {
        return Cave.parse(input).calculateLargesBasinsProduct()
    }

    val testInput = readInput("Day09_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 15) { "Expected 15, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 1134) { "Expected 1134, got $testPart2" }

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
