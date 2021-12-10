fun main() {
    fun part1(input: List<String>): Int {
        var x = 0
        var y = 0
        for (command in input) {
            val (direction, shift) = command.split(" ")
            when (direction) {
                "forward" -> x += shift.toInt()
                "down" -> y += shift.toInt()
                "up" -> y -= shift.toInt()
            }
        }
        return x * y
    }

    fun part2(input: List<String>): Int {
        var x = 0
        var y = 0
        var aim = 0
        for (command in input) {
            val (direction, shift) = command.split(" ")
            when (direction) {
                "down" -> aim += shift.toInt()
                "up" -> aim -= shift.toInt()
                "forward" -> {
                    x += shift.toInt()
                    y += aim * shift.toInt()
                }
            }
        }
        return x * y
    }

    val input = readInput("Day02")
    println(part1(input)) // answer = 2091984
    println(part2(input)) // answer = 2086261056
}
