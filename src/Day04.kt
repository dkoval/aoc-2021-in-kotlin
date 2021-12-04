fun main() {
    data class Cell(val row: Int, val col: Int)

    class Board(size: Int) {
        private val nums = mutableMapOf<Int, Cell>()
        private val countUnmarkedNumsInRow = IntArray(size) { size }
        private val countUnmarkedNumsInCol = IntArray(size) { size }
        private var _sumOfUnmarked = 0

        fun set(row: Int, col: Int, x: Int) {
            nums[x] = Cell(row, col)
            _sumOfUnmarked += x
        }

        fun mark(num: Int): Boolean =
            nums[num]?.let { (row, col) ->
                countUnmarkedNumsInRow[row]--
                countUnmarkedNumsInCol[col]--
                _sumOfUnmarked -= num
                (countUnmarkedNumsInRow[row] == 0) || (countUnmarkedNumsInCol[col] == 0)
            } ?: false

        val sumOfUnmarked: Int get() = _sumOfUnmarked
    }

    val n = 5

    fun readBingoGameInput(input: List<String>): Pair<List<Int>, List<Board>> {
        val nums = input[0].split(",").map { it.toInt() }
        val lines = input.asSequence()
            .drop(1)
            .filter { line -> line.isNotEmpty() }
            .map { line ->
                line.split(" ").asSequence()
                    .filter { it.isNotEmpty() }
                    .map { it.toInt() }
                    .toList()
            }
            .toList()

        val boards = mutableListOf<Board>()
        for (i in lines.indices step n) {
            val board = Board(n)
            repeat(n) { row ->
                lines[i + row].forEachIndexed{ col, x -> board.set(row, col, x) }
            }
            boards += board
        }

        return nums to boards
    }

    fun part1(input: List<String>): Int {
        val (nums, boards) = readBingoGameInput(input)
        for (x in nums) {
            for (board in boards) {
                val won = board.mark(x)
                if (won) {
                    return board.sumOfUnmarked * x
                }
            }
        }
        // answer = 65325
        return -1
    }

    fun part2(input: List<String>): Int {
        val (nums, boards) = readBingoGameInput(input)
        val wonBoards = mutableSetOf<Int>()
        for (x in nums) {
            boards.forEachIndexed { i, board ->
                if (i !in wonBoards) {
                    val won = board.mark(x)
                    if (won) {
                        wonBoards += i
                        if (wonBoards.size == boards.size) {
                            return boards[i].sumOfUnmarked * x
                        }
                    }
                }
            }
        }
        // answer = 4624
        return -1
    }

    check(part1(readInput("Day04_test")) == 4512)
    check(part2(readInput("Day04_test")) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
