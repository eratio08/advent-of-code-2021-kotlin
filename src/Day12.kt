import java.util.*

fun main() {
    val start = "start"
    val end = "end"

    data class Cave(
        val name: String,
    ) {
        val isSmallCave = name.lowercase() == name
        val adjacent: MutableList<Cave> = LinkedList()
        var visited: Boolean = false


        fun setVisitedPart1(value: Boolean) {
            if (isSmallCave && name != end) {
                visited = value
            }
        }

        override fun toString(): String = name
    }

    data class CaveSystem(val startCave: Cave, val cavesByName: Map<String, Cave>) {

        fun dfsPathCount(
            from: Cave = startCave,
            to: Cave = cavesByName[end]!!,
            count: Int = 0
        ): Int {
            val stack = ArrayDeque<Cave>()
            from.setVisitedPart1(true)

            if (from == to) {
                from.setVisitedPart1(false)
                return count + 1
            }
            stack.addAll(from.adjacent.filter { !it.visited })
            var tmpCount = count
            while (stack.isNotEmpty()) {
                val cave = stack.removeLast()
                tmpCount = dfsPathCount(cave, to, tmpCount)
            }
            from.setVisitedPart1(false)
            return tmpCount
        }

//        private val visitedCaves = mutableMapOf<Cave, Int>()
//        private fun setVisited(cave: Cave, visited: Boolean) {
//            if (cave.isSmallCave && cave.name != end && cave.name != start) {
//                visitedCaves.computeIfAbsent(cave) { 0 }
//                val times = visitedCaves[cave]!!
//                if (times < 4) {
//                    cave.visited = visited
//                    visitedCaves[cave] = times + 1
//                }
//            }
//        }
//
//        fun dfsPathCount2(
//            from: Cave = startCave,
//            to: Cave = cavesByName[end]!!,
//            count: Int = 0
//        ): Int {
//            val stack = ArrayDeque<Cave>()
//            setVisited(from, true)
//
//            if (from == to) {
//                setVisited(from, false)
//                return count + 1
//            }
//            stack.addAll(from.adjacent.filter { !it.visited })
//            var tmpCount = count
//            while (stack.isNotEmpty()) {
//                val cave = stack.removeLast()
//                tmpCount = dfsPathCount(cave, to, tmpCount)
//            }
//            setVisited(from, false)
//            return tmpCount
//        }
    }

    fun parseGraph(input: List<String>): CaveSystem {
        val cavesByName = mutableMapOf<String, Cave>()
        input.forEach { line ->
            val parts = line.split("-")
            val c1 = cavesByName.computeIfAbsent(parts[0]) { Cave(parts[0]) }
            val c2 = cavesByName.computeIfAbsent(parts[1]) { Cave(parts[1]) }
            if (!c1.adjacent.contains(c2)) c1.adjacent.add(c2)
            if (!c2.adjacent.contains(c1)) c2.adjacent.add(c1)
        }
        val startCave = cavesByName[start] ?: throw java.lang.IllegalStateException("${cavesByName[start]}")
        return CaveSystem(startCave, cavesByName)
    }

    fun part1(input: List<String>): Int {
        return parseGraph(input).dfsPathCount()
    }

//    fun part2(input: List<String>): Int {
//        return parseGraph(input).dfsPathCount2()
//    }

    val testInput = readInput("Day12_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 226) { "Expected 226, got $testPart1" }
//    val testPart2 = part2(testInput)
//    check(testPart2 == 3509) { "Expected 3509, got $testPart2" }

    val input = readInput("Day12")
    println(part1(input))
//    println(part2(input))
}
