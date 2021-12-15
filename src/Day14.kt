fun main() {

    data class PairInsertion(val pattern: String, val element: String)

    fun parse(input: List<String>): Pair<String, List<PairInsertion>> {
        return input.fold("" to mutableListOf<PairInsertion>()) { (template, insertions), line ->
            if (line == "") {
                // do nothing
                template to insertions
            } else if (line.contains(" -> ")) {
                val parts = line.split(" -> ")
                insertions.add(PairInsertion(parts[0], parts[1]))
                template to insertions
            } else {
                line to insertions
            }
        }
    }

    fun growPolymer(template: String, insertions: List<PairInsertion>): String {
        return template.windowed(2).asSequence().map { part ->
            insertions.firstOrNull { part == it.pattern }
                ?.let { insertion -> "${part[0]}${insertion.element}${part[1]}" }
                ?: part
        }.fold("") { newPoly, part ->
            if (newPoly == "") {
                part
            } else {
                "$newPoly${part.substring(1)}"
            }
        }
    }

    fun growPolymerNSteps(steps: Int, template: String, insertions: List<PairInsertion>): String {
        return (1..steps).fold(template) { t, step ->
            growPolymer(t, insertions).also { println(step) }
        }
    }

    fun calculateElementScore(polymer: String): Int {
        val charactersSorted = polymer.groupBy { it }.entries.sortedBy { it.value.size }
        val smallest = charactersSorted.first().value.size
        val biggest = charactersSorted.last().value.size
        return biggest - smallest
    }

    fun calculateBigElementScore(polymer: String): Long {
        val charactersSorted = polymer.groupBy { it }.entries.sortedBy { it.value.size }
        val smallest = charactersSorted.first().value.fold(0L) { sum, _ -> sum + 1L }
        val biggest = charactersSorted.last().value.fold(0L) { sum, _ -> sum + 1L }
        return biggest - smallest
    }

    fun part1(input: List<String>): Int {
        val (template, insertions) = parse(input)
        val poly = growPolymerNSteps(10, template, insertions)

        return calculateElementScore(poly)
    }

    fun part2(input: List<String>): Long {
        val (template, insertions) = parse(input)
        val poly = growPolymerNSteps(100, template, insertions)

        return calculateBigElementScore(poly)
    }

    val testInput = readInput("Day14_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 1588) { "Expected 1588, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 2188189693529L) { "Expected 2188189693529, got $testPart2" }

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
