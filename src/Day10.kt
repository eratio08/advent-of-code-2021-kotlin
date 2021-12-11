import java.util.*
import kotlin.collections.ArrayDeque

data class Token(val value: Char, val isOpening: Boolean) {
    fun getOpposing(): Token = when (value) {
        '(' -> Token(')', false)
        '[' -> Token(']', false)
        '{' -> Token('}', false)
        '<' -> Token('>', false)

        ')' -> Token('(', true)
        ']' -> Token('[', true)
        '}' -> Token('{', true)
        '>' -> Token('<', true)
        else -> throw IllegalStateException("Unknown token $this")
    }

    fun getScore(): Int =
        when (value) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> 0
        }

    fun getScore2(): Int =
        when (value) {
            ')' -> 1
            ']' -> 2
            '}' -> 3
            '>' -> 4
            else -> 0
        }

    override fun toString(): String = value.toString()

    companion object {
        fun parse(char: Char): Token =
            when (char) {
                '(', '[', '{', '<' -> Token(char, true)
                else -> Token(char, false)
            }
    }
}

object TokenValidator {
    private fun findCorruptedTokens(line: List<Token>): Sequence<Token> {
        val stack: ArrayDeque<Token> = ArrayDeque()
        return line.asSequence().mapNotNull { currentToken ->
            if (currentToken.isOpening) {
                stack.add(currentToken)
                null
            } else {
                val token = stack.removeLast()
                if (token != currentToken.getOpposing()) {
                    currentToken
                } else {
                    null
                }
            }
        }
    }

    private fun findIncompleteChunks(line: List<Token>): List<Token> {
        val stack = LinkedList<Token>()
        line.asSequence().forEach { currentToken ->
            if (currentToken.isOpening) {
                stack.add(currentToken)
            } else {
                stack.removeLast()
            }
        }
        return stack
    }

    fun calculateIncompleteScore(line: List<Token>): Long =
        findIncompleteChunks(line).reversed()
            .asSequence()
            .map { it.getOpposing() }
            .fold(0L) { acc, token -> (acc * 5) + token.getScore2() }

    fun isIncomplete(line: List<Token>): Boolean =
        findFistCorruptedToken(line) == null

    fun findFistCorruptedToken(line: List<Token>): Token? =
        findCorruptedTokens(line).firstOrNull()
}


fun main() {

    fun parseLine(line: String): List<Token> =
        line.asSequence()
            .map(Token.Companion::parse)
            .toList()

    fun part1(input: List<String>): Int {
        return input.asSequence()
            .mapNotNull { line -> parseLine(line).let { TokenValidator.findFistCorruptedToken(it) } }
            .fold(0) { sum, t -> t.getScore() + sum }
    }

    fun part2(input: List<String>): Long {
        val scores = input.asSequence()
            .map { line -> parseLine(line) }
            .filter { line -> TokenValidator.isIncomplete(line) }
            .map { line -> TokenValidator.calculateIncompleteScore(line) }
            .sorted()
            .toList()
        return scores[scores.size / 2]
    }

    val testInput = readInput("Day10_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 26397) { "Expected 26397, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 288957L) { "Expected 288957, got $testPart2" }

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
