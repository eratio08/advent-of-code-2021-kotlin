import java.math.BigInteger

class RotatingBuckets(private val buckets: Array<BigInteger>) {

    fun step(): RotatingBuckets {
        val zeroes = buckets.first()
        // shift left
        val rest = buckets.drop(1)
        val newBuckets = Array(9) { BigInteger.ZERO }
        rest.forEachIndexed { index, i -> newBuckets[index] = i }
        // append new 8s
        newBuckets[8] = zeroes
        // add new 6s
        newBuckets[6] = newBuckets[6] + zeroes
        return RotatingBuckets(newBuckets)
    }

    fun sum(): BigInteger =
        buckets.reduce(BigInteger::add)

    companion object {
        fun parse(numbers: String): RotatingBuckets {
            val days = numbers.split(",")
                .map { it.toInt() }
                .groupBy { it }

            val buckets = days.entries
                .fold(Array(9) { BigInteger.ZERO }) { buckets, (k, vs) ->
                    buckets[k] = BigInteger.valueOf(vs.size.toLong())
                    buckets
                }
            return RotatingBuckets(buckets)
        }
    }
}

fun main() {
    fun part1(input: List<String>): BigInteger {
        return generateSequence(RotatingBuckets.parse(input.first()).step()) { it.step() }.take(80).last().sum()
    }

    fun part2(input: List<String>): BigInteger {
        return generateSequence(RotatingBuckets.parse(input.first()).step()) { it.step() }.take(256).last().sum()
    }

    val testInput = readInput("Day06_test")
    val testPart1 = part1(testInput)
    check(testPart1 == BigInteger.valueOf(5934L)) { "Expected 5934, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == BigInteger.valueOf(26984457539)) { "Expected 26984457539, got $testPart2" }

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
