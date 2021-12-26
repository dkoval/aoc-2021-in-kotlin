enum class Action {
    ON, OFF
}

fun main() {
    data class Cube(val x: Int, val y: Int, val z: Int)

    data class Cuboid(val xs: IntRange, val ys: IntRange, val zs: IntRange) {

        val numCubes: Long = (xs.last - xs.first + 1L) * (ys.last - ys.first + 1L) * (zs.last - zs.first + 1L)

        fun intersect(that: Cuboid): Cuboid? {
            if (xs.disjoint(that.xs) || ys.disjoint(that.ys) || zs.disjoint(that.zs)) {
                return null
            }
            return Cuboid(xs.intersect(that.xs), ys.intersect(that.ys), zs.intersect(that.zs))
        }

        private fun IntRange.disjoint(that: IntRange): Boolean =
            this.first > that.last || this.last < that.first

        private fun IntRange.intersect(that: IntRange): IntRange =
            maxOf(this.first, that.first)..minOf(this.last, that.last)
    }

    data class Instruction(val action: Action, val cuboid: Cuboid)

    fun readInstructions(lines: List<String>): List<Instruction> =
        lines.asSequence()
            .map { line -> line.split(" ") }
            .map { (action, ranges) ->
                val (xs, ys, zs) = ranges.split(",").asSequence()
                    .map { it.substringAfter("=") }
                    .map { range ->
                        val (first, last) = range.split("..").map { it.toInt() }
                        IntRange(first, last)
                    }
                    .toList()

                Instruction(
                    action = enumValueOf(action.uppercase()),
                    cuboid = Cuboid(xs, ys, zs)
                )
            }
            .toList()

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
        val instructions = readInstructions(input)
        val counts = mutableMapOf<Cuboid, Int>()
        for ((action, currCuboid) in instructions) {
            // utilise inclusion-exclusion principle to intersect a new cuboid with all the previous ones
            val newCounts = mutableMapOf<Cuboid, Int>()
            for ((prevCuboid, sign) in counts.entries) {
                val intersection = currCuboid.intersect(prevCuboid) ?: continue
                newCounts[intersection] = (newCounts[intersection] ?: 0) - sign
            }

            if (action == Action.ON) {
                newCounts[currCuboid] = (newCounts[currCuboid] ?: 0) + 1
            }

            for ((cuboid, sign) in newCounts) {
                counts[cuboid] = (counts[cuboid] ?: 0) + sign
            }
        }
        return counts.entries.sumOf { (cuboid, sign) ->  cuboid.numCubes * sign }
    }

    check(part1(readInput("Day22_test_part1_1")) == 39)
    check(part1(readInput("Day22_test_part1_2")) == 590784)
    check(part2(readInput("Day22_test_part2_1")) == 2758514936282235)

    val input = readInput("Day22")
    println(part1(input)) // answer = 611378
    println(part2(input)) // answer = 1214313344725528
}
