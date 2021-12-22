enum class Action {
    ON, OFF
}

fun main() {
    data class Cube(val x: Int, val y: Int, val z: Int)
    data class Cuboid(val xs: IntRange, val ys: IntRange, val zs: IntRange)
    data class Instruction(val action: Action, val cuboid: Cuboid)

    fun readInstructions(lines: List<String>): List<Instruction> {
        fun range(range: String): IntRange {
            val (min, max) = range.split("..")
            return min.toInt()..max.toInt()
        }

        return lines.asSequence()
            .map { line -> line.split(" ") }
            .map { (action, ranges) ->
                val (xs, ys, zs) = ranges.split(",").map { it.substringAfter("=") }
                Instruction(
                    action = enumValueOf(action.uppercase()),
                    cuboid = Cuboid(range(xs), range(ys), range(zs))
                )
            }
            .toList()
    }

    fun part1(input: List<String>): Int {
        val instructions = readInstructions(input)
        val cubes = mutableSetOf<Cube>()
        for ((action, cuboid) in instructions) {
            for (x in maxOf(cuboid.xs.first, -50)..minOf(cuboid.xs.last, 50)) {
                for (y in maxOf(cuboid.ys.first, -50)..minOf(cuboid.ys.last, 50)) {
                    for (z in maxOf(cuboid.zs.first, -50)..minOf(cuboid.zs.last, 50)) {
                        when (action) {
                            Action.ON -> cubes += Cube(x, y, z)
                            Action.OFF -> cubes -= Cube(x, y, z)
                        }
                    }
                }
            }
        }
        return cubes.size
    }

    fun part2(input: List<String>): Long {
        TODO()
    }

    check(part1(readInput("Day22_test_part1_1")) == 39)
    check(part1(readInput("Day22_test_part1_2")) == 590784)
    //check(part2(readInput("Day22_test_part2_1")) == 2758514936282235)

    val input = readInput("Day22")
    println(part1(input)) // answer = 611378
    //println(part2(input)) // answer = ?
}
