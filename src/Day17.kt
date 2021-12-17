import kotlin.math.abs

class Area(val xRange: IntRange, val yRange: IntRange)

class Point(val x: Int, val y: Int) {

    fun within(target: Area): Boolean = x in target.xRange && y in target.yRange
}

fun main() {
    fun readTargetArea(input: List<String>): Area =
        input.first()
            .removePrefix("target area: ")
            .split(", ")
            .asSequence()
            .map {
                // drop "x=" and "y=" prefixes
                val idx = it.indexOf('=')
                it.drop(idx + 1)
            }
            .map { range -> range.split("..") }
            .map { (first, last) -> first.toInt()..last.toInt() }
            .toList()
            .let { (xRange, yRange) -> Area(xRange, yRange) }

    fun hit(target: Area, initialVelocity: Point): Pair<Boolean, Int> {
        fun outside(position: Point, velocity: Point): Boolean =
            (velocity.x > 0 && position.x > target.xRange.last) ||
                    (velocity.x < 0 && position.x < target.xRange.first) ||
                    (velocity.y < 0 && position.y < target.yRange.first)

        fun move(position: Point, velocity: Point): Pair<Point, Point> {
            val newPosition = Point(
                position.x + velocity.x,
                position.y + velocity.y
            )
            val newVelocity = Point(
                when {
                    velocity.x > 0 -> velocity.x - 1
                    velocity.x < 0 -> velocity.x + 1
                    else -> 0
                },
                velocity.y - 1
            )
            return newPosition to newVelocity
        }

        var position = Point(0, 0)
        var velocity = initialVelocity
        var maxY = 0
        while (!outside(position, velocity)) {
            maxY = maxOf(maxY, position.y)
            if (position.within(target)) {
                return true to maxY
            }
            val (newPosition, newVelocity) = move(position, velocity)
            position = newPosition
            velocity = newVelocity
        }
        return false to Int.MIN_VALUE
    }

    fun part1(input: List<String>): Int {
        val target = readTargetArea(input)

        // start off with the highest Y possible
        var yVelocity = abs(target.yRange.first)
        // simulate probes
        while (yVelocity >= target.yRange.first) {
            for (xVelocity in -100..101) {
                // try to hit the target area
                val (success, maxYVelocity) = hit(target, Point(xVelocity, yVelocity))
                if (success) {
                    return maxYVelocity
                }
            }
            yVelocity--
        }
        return Int.MIN_VALUE
    }

    fun part2(input: List<String>): Int {
        val target = readTargetArea(input)

        // start off with the highest Y possible
        var yVelocity = abs(target.yRange.first)
        // simulate probes and count successful ones
        var count = 0
        while (yVelocity >= target.yRange.first) {
            for (xVelocity in -100..101) {
                // try to hit the target area
                val (success, _) = hit(target, Point(xVelocity, yVelocity))
                if (success) {
                    count++
                }
            }
            yVelocity--
        }
        return count
    }

    check(part1(readInput("Day17_test")) == 45)
    check(part2(readInput("Day17_test")) == 112)

    val input = readInput("Day17")
    println(part1(input)) // answer = 23005
    println(part2(input)) // answer = 2040
}
