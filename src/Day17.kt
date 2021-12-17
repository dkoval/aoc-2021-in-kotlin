import kotlin.math.abs

class Area(val xRange: IntRange, val yRange: IntRange)

class Point(val x: Int, val y: Int) {

    fun within(target: Area): Boolean = x in target.xRange && y in target.yRange
}

class Position(val point: Point, val velocity: Point) {

    fun onWayTo(target: Area): Boolean {
        fun notOnWayTo(): Boolean =
            (velocity.x > 0 && point.x > target.xRange.last) ||
                    (velocity.x < 0 && point.x < target.xRange.first) ||
                    (velocity.y < 0 && point.y < target.yRange.first)

        return !notOnWayTo()
    }

    fun next(): Position = Position(
        point = Point(
            point.x + velocity.x,
            point.y + velocity.y
        ),
        velocity = Point(
            when {
                velocity.x > 0 -> velocity.x - 1
                velocity.x < 0 -> velocity.x + 1
                else -> 0
            },
            velocity.y - 1
        )
    )
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

    fun hit(target: Area, initialPosition: Position): Pair<Boolean, Int> {
        var position = initialPosition
        var maxY = 0
        while (position.onWayTo(target)) {
            maxY = maxOf(maxY, position.point.y)
            if (position.point.within(target)) {
                return true to maxY
            }
            position = position.next()
        }
        return false to Int.MIN_VALUE
    }

    fun part1(input: List<String>): Int {
        val target = readTargetArea(input)

        // start off with the highest Y possible
        val maxYVelocity = abs(target.yRange.first)
        // simulate probes
        for (yVelocity in maxYVelocity downTo target.yRange.first) {
            for (xVelocity in -100..101) {
                // try to hit the target area
                val (success, maxY) = hit(target, Position(point = Point(0, 0), velocity = Point(xVelocity, yVelocity)))
                if (success) {
                    return maxY
                }
            }
        }
        return Int.MIN_VALUE
    }

    fun part2(input: List<String>): Int {
        val target = readTargetArea(input)

        // start off with the highest Y possible
        val maxYVelocity = abs(target.yRange.first)
        // simulate probes and count successful ones
        var count = 0
        for (yVelocity in maxYVelocity downTo target.yRange.first) {
            for (xVelocity in -100..101) {
                // try to hit the target area
                val (success, _) = hit(target, Position(point = Point(0, 0), velocity = Point(xVelocity, yVelocity)))
                if (success) {
                    count++
                }
            }
        }
        return count
    }

    check(part1(readInput("Day17_test")) == 45)
    check(part2(readInput("Day17_test")) == 112)

    val input = readInput("Day17")
    println(part1(input)) // answer = 23005
    println(part2(input)) // answer = 2040
}
