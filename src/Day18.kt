import java.util.*

sealed class SnailfishNumber
data class LiteralSnailfishNumber(val num: Int) : SnailfishNumber()
data class SnailfishNumberPair(val x: SnailfishNumber, val y: SnailfishNumber) : SnailfishNumber() {
    val isLiteralPair = x is LiteralSnailfishNumber && y is LiteralSnailfishNumber
}


fun main() {
    data class ReductionResult(val num: SnailfishNumber?, val leftExcess: Int = 0, val rightExcess: Int = 0)

    fun bottomUp(line: String): SnailfishNumber {
        val charStack = Stack<Char>()
        val numStack = Stack<SnailfishNumber>()

        line.forEach {
            when (it) {
                ']' -> {
                    var n = charStack.pop()
                    val snd = if (n == ',') {
                        numStack.pop()
                    } else {
                        LiteralSnailfishNumber(n.toString().toInt()).also {
                            charStack.pop()
                        }
                    }
                    n = charStack.pop()
                    val fst = if (n == '[') {
                        numStack.pop()
                    } else {
                        LiteralSnailfishNumber(n.toString().toInt()).also {
                            charStack.pop()
                        }
                    }
                    numStack.push(SnailfishNumberPair(fst, snd))
                }
                else -> charStack.push(it)
            }
        }

        return numStack.pop()
    }

    fun parse(lines: List<String>): List<SnailfishNumber> {
        return lines.map { bottomUp(it) }
    }

    fun reduce(num: SnailfishNumber, level: Int = 0): ReductionResult {
        return when (num) {
            is LiteralSnailfishNumber -> {
                if (num.num > 9) {
                    val half = num.num / 2
                    val otherHalf = num.num - half
                    ReductionResult(
                        SnailfishNumberPair(LiteralSnailfishNumber(half), LiteralSnailfishNumber(otherHalf))
                    )
                } else {
                    ReductionResult(num)
                }
            }
            is SnailfishNumberPair -> when {
                level >= 4 -> {
                    val (x, y) = num
                    // explode
                    if (x is LiteralSnailfishNumber && y is LiteralSnailfishNumber) {
                        return ReductionResult(null, x.num, y.num)
                    }

                    // like else
                    TODO()
                }
                else -> {
                    val (rNum, rrEx, rlEx) = reduce(num.x)
                    val (lNum, lrEx, llEx) = reduce(num.y)
                    when {
                        (rNum != null && lNum != null) -> ReductionResult(
                            SnailfishNumberPair(rNum, lNum),
                            rrEx + lrEx,
                            rlEx + llEx
                        )
                        rNum == null && lNum == null -> ReductionResult(
                            null,
                            rrEx + lrEx,
                            rlEx + llEx
                        )
                        rNum == null -> {

                            ReductionResult(SnailfishNumberPair(LiteralSnailfishNumber(0)))
                        }
                        else -> TODO()
                    }
                    TODO()
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val x = parse(input)
        TODO()
    }

    fun part2(input: List<String>): Long {
        TODO()
    }

    val testInput = readInput("Day18_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 45) { "Expected 45, got $testPart1" }
//    val testPart2 = part2(testInput)
//    check(testPart2 == 1L) { "Expected 1, got $testPart2" }

    val input = readInput("Day18")
    println(part1(input))
//    println(part2(input))
}
