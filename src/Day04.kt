const val SQUARE_DIMENSION = 5

data class BingoNumber(val num: Int, var isMarked: Boolean = false) {
    override fun toString(): String =
        if (isMarked) "$num!" else "$num"
}

data class BingoSquare(val numbers: List<BingoNumber>, var hasFinished: Boolean = false) {
    private fun getIdx(x: Int, y: Int): Int =
        (y * SQUARE_DIMENSION) + x

    fun markNumber(num: Int): Boolean {
        return numbers.asSequence()
            .filter { it.num == num }
            .map { it.isMarked = true; it }
            .toList().isNotEmpty()
    }

    fun hasBingo(): Boolean =
        (hasAnyBingo(getRows()) || hasAnyBingo(getColumns())).also { hasFinished = it }

    private fun hasAnyBingo(numbers: List<List<BingoNumber>>): Boolean =
        numbers.fold(false) { hasBingo, row -> hasBingo || hasBingo(row) }

    private fun hasBingo(numbers: List<BingoNumber>): Boolean =
        numbers.fold(true) { hasBingo, num -> hasBingo && num.isMarked }

    private fun getRows(): List<List<BingoNumber>> =
        numbers.windowed(5, 5)

    private fun getColumns(): List<List<BingoNumber>> =
        (0 until SQUARE_DIMENSION).fold(mutableListOf<MutableList<BingoNumber>>()) { columns, x ->
            columns.add((0 until SQUARE_DIMENSION).fold(mutableListOf<BingoNumber>()) { column, y ->
                column.add(numbers[getIdx(x, y)])
                column
            })
            columns
        }

    fun getUnmarkedSum(): Int =
        numbers.asSequence()
            .filter { !it.isMarked }
            .map { it.num }
            .reduce(Int::plus)

    override fun toString(): String =
        getRows()
            .joinToString("\n") {
                it.joinToString(" ") { num ->
                    num.toString().padStart(3)
                }
            }

    companion object {
        fun build(lines: List<String>): BingoSquare =
            BingoSquare(
                lines.asSequence()
                    .take(5)
                    .flatMap { it.trim().split("""\s+""".toRegex()) }
                    .map { it.toInt() }
                    .map { BingoNumber(it) }
                    .toList()
            )
    }
}

data class BingoGame(val squares: List<BingoSquare>, val numbers: List<Int>) {

    fun playUntilFirstBingo(): Int {
        val (finalNum, square) = getFinalNumberAndBoard()
        return finalNum * square.getUnmarkedSum()
    }

    fun playUntilLastBingo(): Int {
        return numbers.fold(mutableListOf<Pair<Int, BingoSquare>>()) { bingoSquares, num ->
            squares.filter { !it.hasFinished }
                .mapNotNull {
                    if (it.markNumber(num) && it.hasBingo()) num to it
                    else null
                }
                .forEach { bingoSquares.add(it) }
            bingoSquares
        }
            .lastOrNull()
            ?.let { (num, square) ->
                num * square.getUnmarkedSum()
            }
            ?: 0
    }

    private fun getFinalNumberAndBoard(): Pair<Int, BingoSquare> {
        for (num in numbers) {
            for (square in squares) {
                val wasMarked = square.markNumber(num)
                if (wasMarked) {
                    val hasBingo = square.hasBingo()
                    if (hasBingo) {
                        return num to square
                    }
                }
            }
        }
        throw IllegalStateException()
    }

    companion object {
        fun build(input: List<String>): BingoGame {
            val numbers = input.first().split(",").map { it.toInt() }
            val squares = input.asSequence()
                .drop(2)
                .windowed(6, 6, true)
                .fold(mutableListOf<BingoSquare>()) { squares, lines ->
                    squares.add(BingoSquare.build(lines))
                    squares
                }
            return BingoGame(squares, numbers)
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return BingoGame.build(input).playUntilFirstBingo()
    }

    fun part2(input: List<String>): Int {
        return BingoGame.build(input).playUntilLastBingo()
    }

    val testInput = readInput("Day04_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 4512) { "Expected 4512, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 1924) { "Expected 1924, got $testPart2" }

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
