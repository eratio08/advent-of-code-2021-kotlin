import kotlin.math.abs

data class DigitColumn(val position: Int, val ones: Int = 0, val zeros: Int = 0) {
    fun mostCommon() = if (ones > zeros) 1 else 0
    fun leastCommon() = abs(mostCommon() - 1)
    fun mostCommonEqual() = if (ones >= zeros) 1 else 0
    fun leastCommonEqual() = if (zeros <= ones) 0 else 1
    fun addBit(bitStr: String) = if (bitStr == "1") copy(ones = ones + 1) else copy(zeros = zeros + 1)

    companion object {
        fun buildRow(input: List<String>): List<DigitColumn> {
            val digits = input.map { it.windowed(1) }
            return buildFromDigits(digits)
        }

        fun buildFromDigits(digits: List<List<String>>): List<DigitColumn> =
            digits.fold(mutableListOf()) { acc, digs ->
                digs.forEachIndexed { i, it ->
                    if (acc.size == i) {
                        acc.add(DigitColumn(i))
                    }
                    acc[i] = acc[i].addBit(it)
                }
                acc
            }
    }
}

data class Rate(val gamma: Int = 0, val epsilon: Int = 0) {
    fun result() = gamma * epsilon
    fun addDigit(digit: DigitColumn) =
        copy(
            gamma = gamma.shl(1) + digit.mostCommon(),
            epsilon = epsilon.shl(1) + digit.leastCommon(),
        )
}

fun main() {
    fun part1(input: List<String>): Int {
        return DigitColumn.buildRow(input)
            .fold(Rate()) { acc, digit -> acc.addDigit(digit) }
            .result()
    }

    fun part2(input: List<String>): Int {
        val digits = input.map { it.windowed(1) }

        val oxPredicate =
            { it: List<String>, column: DigitColumn -> it[column.position].toInt() == column.mostCommonEqual() }
        val o2Predicate =
            { it: List<String>, column: DigitColumn -> it[column.position].toInt() == column.leastCommonEqual() }

        fun findByCriteria(digitLines: List<List<String>>, criteria: (List<String>, DigitColumn) -> Boolean): Int {
            var res: List<List<String>> = digitLines
            var pos = 0
            while (res.size != 1) {
                val columns = DigitColumn.buildFromDigits(res)
                res = res.filter { criteria(it, columns[pos]) }
                pos += 1
            }
            return res[0].fold(0) { acc, it -> acc.shl(1) + it.toInt() }
        }

        val x1 = findByCriteria(digits, oxPredicate)
        val x2 = findByCriteria(digits, o2Predicate)
        return x1 * x2
    }

    val testInput = readInput("Day03_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 198) { "Expected 198, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 230) { "Expected 230, got $testPart2" }

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
