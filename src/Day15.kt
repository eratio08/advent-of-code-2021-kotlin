import java.util.*

fun main() {
    class Chiton(var riskLevel: Int, val adjacent: MutableSet<Chiton> = mutableSetOf()) {
        override fun toString(): String = riskLevel.toString()
    }

    fun parse(input: List<String>): List<Chiton> {
        val height = input.size
        val width = input.first().length

        val chitonMatrix = input.map { line ->
            line.asSequence()
                .map { riskLevelStr -> riskLevelStr.toString().toInt() }
                .map { riskLevel -> Chiton(riskLevel) }
                .toList()
        }

        fun inBounds(x: Int, y: Int): Boolean =
            x in (0 until width) && y in (0 until height)

        val directions = listOf(0 to -1, 1 to 0, 1 to 0, -1 to 0)
        fun getNeighbours(x: Int, y: Int): Sequence<Pair<Int, Int>> =
            directions.asSequence()
                .map { (dx, dy) -> x + dx to y + dy }
                .filter { (xx, yy) -> inBounds(xx, yy) }

        (0 until height).forEach { y ->
            (0 until width).forEach { x ->
                val chiton = chitonMatrix[y][x]
                getNeighbours(x, y)
                    .map { (xx, yy) -> chitonMatrix[yy][xx] }
                    .forEach { neighbourChiton ->
                        neighbourChiton.adjacent.add(chiton)
                        chiton.adjacent.add(neighbourChiton)
                    }
            }
        }
        return chitonMatrix.asSequence().flatMap { it.asSequence() }.toList()
    }

    fun dijkstraShortestPath(from: Chiton, to: Chiton, chitons: List<Chiton>): LinkedList<Chiton> {
        val distances = mutableMapOf<Chiton, Int>()
        val previous = mutableMapOf<Chiton, Chiton?>()
        val queue = LinkedList<Chiton>()
        queue.addAll(chitons)

        chitons.forEach { chiton ->
            if (chiton == from) {
                distances[chiton] = 0
            } else {
                distances[chiton] = Int.MAX_VALUE
            }
            previous[chiton] = null
        }

        while (queue.isNotEmpty()) {
            val chiton = queue.removeFirst()

            if (chiton == to) {
                break
            }

            chiton.adjacent
                .filter { queue.contains(it) }
                .forEach { naighbourChiton ->
                    val alternativeRoute = distances[chiton]!! + naighbourChiton.riskLevel
                    if (alternativeRoute < distances[naighbourChiton]!!) {
                        distances[naighbourChiton] = alternativeRoute
                        previous[naighbourChiton] = chiton
                    }
                }
        }

        val path = LinkedList<Chiton>()
        var target: Chiton? = to
        if (previous[target] != null || target == from) {
            while (target != null) {
                path.addFirst(target)
                target = previous[target]
            }
        }
        return path
    }

    fun part1(input: List<String>): Int {
        val chitons = parse(input)
        val from = chitons.first()
        val to = chitons.last()
        val path = dijkstraShortestPath(from, to, chitons)
        return path.drop(1).fold(0) { sum, chiton -> sum + chiton.riskLevel }
    }

    fun part2(input: List<String>): Long {
        TODO()
    }

    val testInput = readInput("Day15_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 40) { "Expected 40, got $testPart1" }
//    val testPart2 = part2(testInput)
//    check(testPart2 == 2188189693529L) { "Expected 2188189693529, got $testPart2" }

    val input = readInput("Day15")
    println(part1(input))
//    println(part2(input))
}
