sealed class Command(val value: Int) {
    abstract operator fun invoke(location: Location): Location
    abstract operator fun invoke(location: LocationWithAim): LocationWithAim

    class Forward(value: Int) : Command(value) {
        override fun invoke(location: Location): Location =
            location.copy(x = location.x + value)

        override fun invoke(location: LocationWithAim): LocationWithAim =
            location.copy(x = location.x + value, y = location.y + value * location.aim)
    }

    class Down(value: Int) : Command(value) {
        override fun invoke(location: Location): Location =
            location.copy(y = location.y + value)

        override fun invoke(location: LocationWithAim): LocationWithAim =
            location.copy(aim = location.aim + value)
    }

    class Up(value: Int) : Command(value) {
        override fun invoke(location: Location): Location =
            location.copy(y = location.y - value)

        override fun invoke(location: LocationWithAim): LocationWithAim =
            location.copy(aim = location.aim - value)
    }

    companion object {
        fun from(str: String): Command {
            val (command, value) = str.split(" ")
            return from(command, value.toInt())
        }

        private fun from(str: String, value: Int): Command =
            when (str) {
                "forward" -> Forward(value)
                "down" -> Down(value)
                "up" -> Up(value)
                else -> throw IllegalArgumentException()
            }
    }
}

data class Location(val x: Int = 0, val y: Int = 0) {
    fun product(): Int = x * y
}

data class LocationWithAim(val x: Int = 0, val y: Int = 0, val aim: Int = 0) {
    fun product(): Int = x * y
}

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .asSequence()
            .map(Command.Companion::from)
            .fold(Location()) { location, cmd ->
                cmd(location)
            }.let(Location::product)
    }

    fun part2(input: List<String>): Int {
        return input
            .asSequence()
            .map(Command.Companion::from)
            .fold(LocationWithAim()) { location, cmd ->
                cmd(location)
            }.let(LocationWithAim::product)
    }

    val testInput = readInput("Day02_test")
    val testPart1 = part1(testInput)
    check(testPart1 == 150) { "Expected 150, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 900) { "Expected 900, got $testPart2" }

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
