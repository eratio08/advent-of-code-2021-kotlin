import java.util.*
import kotlin.math.min

sealed class Packet(val version: Int, val type: Int) {
    abstract fun eval(): Long
}

class Literal(version: Int, type: Int, val content: Long) : Packet(version, type) {
    override fun eval(): Long = content
}

class Operator(version: Int, type: Int, val subPackets: List<Packet>) : Packet(version, type) {
    override fun eval(): Long {
        return when (type) {
            0 -> subPackets.fold(0L) { sum, i -> sum + i.eval() }
            1 -> subPackets.fold(1L) { prod, i -> prod * i.eval() }
            2 -> subPackets.minOf { it.eval() }
            3 -> subPackets.maxOf { it.eval() }
            5 -> {
                require(subPackets.size == 2)
                if (subPackets[0].eval() > subPackets[1].eval()) 1 else 0
            }
            6 -> {
                require(subPackets.size == 2)
                if (subPackets[0].eval() < subPackets[1].eval()) 1 else 0
            }
            7 -> {
                require(subPackets.size == 2)
                if (subPackets[0].eval() == subPackets[1].eval()) 1 else 0
            }
            else -> throw IllegalStateException(type.toString())
        }
    }
}

fun main() {

    fun hexToBit(c: Char): String =
        when (c) {
            '0' -> "0000"
            '1' -> "0001"
            '2' -> "0010"
            '3' -> "0011"
            '4' -> "0100"
            '5' -> "0101"
            '6' -> "0110"
            '7' -> "0111"
            '8' -> "1000"
            '9' -> "1001"
            'A' -> "1010"
            'B' -> "1011"
            'C' -> "1100"
            'D' -> "1101"
            'E' -> "1110"
            'F' -> "1111"
            else -> throw IllegalArgumentException("Unknown hex char $c")
        }

    fun fromHex(line: String): String =
        line.map(::hexToBit).joinToString("")

    fun bitToInt(bits: List<Char>): Int =
        bits.joinToString("").toInt(2)

    fun <E> LinkedList<E>.removeN(i: Int): List<E> =
        (0 until min(i, this.size)).map { this.removeFirst() }

    fun parse(bitQueue: LinkedList<Char>): Packet? {
        if (bitQueue.isEmpty()) {
            return null
        }
        val version = bitToInt(bitQueue.removeN(3))
        when (val type = bitToInt(bitQueue.removeN(3))) {
            4 -> {
                val valueQueue = LinkedList<String>()
                var hasMore = true
                while (hasMore) {
                    hasMore = bitQueue.removeFirst() == '1'
                    val literalValue = bitQueue.removeN(4)
                    valueQueue.add(literalValue.joinToString(""))
                }
                return Literal(
                    version = version,
                    type = type,
                    content = valueQueue.joinToString("").toLong(2)
                )
            }
            else -> {
                when (bitQueue.removeFirst()) {
                    '0' -> {
                        val totalLengthBits = bitQueue.removeN(15)
                        val subPacketLength = bitToInt(totalLengthBits)
                        val subPacketsBitsQueue = LinkedList<Char>().also {
                            it.addAll(bitQueue.removeN(subPacketLength))
                        }
                        val subPackets = mutableListOf<Packet>()
                        while (subPacketsBitsQueue.isNotEmpty()) {
                            val subPacket = parse(subPacketsBitsQueue)
                            subPacket?.let { subPackets.add(it) }
                        }
                        return Operator(version, type, subPackets)
                    }
                    '1' -> {
                        val subPacketNum = bitToInt(bitQueue.removeN(11))
                        val subPackets = (0 until subPacketNum).mapNotNull { parse(bitQueue) }
                        return Operator(version, type, subPackets)
                    }
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

    fun parse(line: String): Packet? {
        val bits = fromHex(line)
        val bitQueue = LinkedList<Char>().also { it.addAll(bits.asSequence().toList()) }
        return parse(bitQueue)
    }

    fun sumPacketVersions(packet: Packet): Int =
        when (packet) {
            is Literal -> packet.version
            is Operator -> packet.version + packet.subPackets.fold(0) { s, subP -> s + sumPacketVersions(subP) }
        }

    fun part1(input: List<String>): Int {
        val line = input.first()
        val packet = parse(line)
        return sumPacketVersions(packet!!)
    }

    fun part2(input: List<String>): Long {
        val line = input.first()
        val packet = parse(line)
        return packet?.eval() ?: -1
    }

    val testInput = readInput("Day16_test")
//    val testPart1 = part1(testInput)
//    check(testPart1 == 31) { "Expected 31, got $testPart1" }
    val testPart2 = part2(testInput)
    check(testPart2 == 1L) { "Expected 1, got $testPart2" }

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}
