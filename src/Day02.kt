fun main() {
    fun part1(input: List<String>): Int {
        var x = 0
        var y = 0
        for (command in input) {
            val (name, shift) = command.split(" ")
            when (name) {
                "forward" -> x += shift.toInt()
                "down" -> y += shift.toInt()
                "up" -> y -= shift.toInt()
            }
        }
        // answer = 2091984
        return x * y
    }

    fun part2(input: List<String>): Int {
        var x = 0
        var y = 0
        var aim = 0
        for (command in input) {
            val (name, shift) = command.split(" ")
            when (name) {
                "down" -> aim += shift.toInt()
                "up" -> aim -= shift.toInt()
                "forward" -> {
                    x += shift.toInt()
                    y += aim * shift.toInt()
                }
            }
        }
        // answer = 2086261056
        return x * y
    }

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
