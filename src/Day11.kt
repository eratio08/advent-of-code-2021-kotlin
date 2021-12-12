data class DumboOctopus(
    var energyLevel: Int,
    var neighbours: List<DumboOctopus> = listOf(),
    var hasFlashed: Boolean = false
) {
    fun raiseEnergyLevel(): DumboOctopus {
        when (energyLevel + 1 > 9) {
            true -> {
                energyLevel = 0
                hasFlashed = true
            }
            false -> {
                energyLevel += 1
            }
        }
        return this
    }

    override fun toString(): String = "$energyLevel"
}

data class DumboCave(val octopuses: List<DumboOctopus>, var flashCount: Int = 0) {

    override fun toString(): String =
        octopuses.mapIndexed { i, it ->
            val str = it.toString()
            if ((i + 1) % 10 == 0 && i != 0) str + "\n"
            else str
        }.joinToString("")

    private fun doStep(): Int {
        val haveFlashed = octopuses.asSequence()
            .map { octopus -> octopus.raiseEnergyLevel() }
            .filter { it.hasFlashed }
            .toList()
        raiseAdjacentEnergy(haveFlashed).forEach {
            it.energyLevel = 0
            it.hasFlashed = false
            flashCount += 1
        }
        return flashCount
    }

    private fun raiseAdjacentEnergy(haveFlashed: List<DumboOctopus>): List<DumboOctopus> {
        return if (haveFlashed.isEmpty()) {
            haveFlashed
        } else {
            haveFlashed + raiseAdjacentEnergy(
                haveFlashed.fold(mutableListOf<DumboOctopus>()) { newFlashed, hasFlashed ->
                    val flashed = hasFlashed.neighbours
                        .asSequence()
                        .filter { !it.hasFlashed }
                        .map { it.raiseEnergyLevel() }
                        .filter { it.hasFlashed }
                        .toList()
                    newFlashed.addAll(flashed)
                    newFlashed
                }
            )
        }
    }

    private fun doSteps(steps: Int) {
        (1..steps).forEach { _ -> this.doStep() }
    }

    fun countFlashesAfterSteps(steps: Int): Int {
        doSteps(steps)
        return flashCount
    }

    fun findTotalFlashStep(): Int {
        var needsMoreSteps = true
        var step = 0
        while (needsMoreSteps) {
            val initialCount = flashCount + 0
            val diff = doStep() - initialCount
            needsMoreSteps = !(diff == 100)
            step += 1
        }
        return step
    }

    companion object {
        private val directions = listOf(0 to -1, 1 to -1, 1 to 0, 1 to 1, 0 to 1, -1 to 1, -1 to 0, -1 to -1)

        fun parse(input: List<String>): DumboCave {
            val octopusesInGrid = input.map { str ->
                str.asSequence()
                    .map { it.toString().toInt() }
                    .map { DumboOctopus(it) }
                    .toList()
            }
            val height = input.size

            fun isInBounds(x: Int, y: Int): Boolean =
                x in (0 until height) && y in (0 until height)

            fun generateNeighbourCoordinates(x: Int, y: Int): Sequence<Pair<Int, Int>> =
                directions.asSequence()
                    .map { (dx, dy) -> (x + dx) to (y + dy) }
                    .filter { (cx, cy) -> isInBounds(cx, cy) }

            fun getNeighboursOf(x: Int, y: Int): List<DumboOctopus> =
                generateNeighbourCoordinates(x, y).map { (cx, cy) -> octopusesInGrid[cy][cx] }.toList()

            val octopuses = (0 until height).flatMap { y ->
                (0 until height).map { x ->
                    val octopus = octopusesInGrid[y][x]
                    octopus.neighbours = getNeighboursOf(x, y)
                    octopus
                }
            }

            require(octopuses.none { it.neighbours.isEmpty() })

            return DumboCave(octopuses)
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return DumboCave.parse(input).countFlashesAfterSteps(100)
    }

    fun part2(input: List<String>): Int {
        return DumboCave.parse(input).findTotalFlashStep()
    }

    val testInput = readInput("Day11_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 1656) { "Expected 1656, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 195) { "Expected 195, got $testPart2" }

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
