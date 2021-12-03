fun main() {
    fun part1(input: List<String>): Int {
        var numBitsInNum = -1
        val numOnes = arrayListOf<Int>()
        for (num in input) {
            numBitsInNum = maxOf(numBitsInNum, num.length)
            for (i in 0 until numBitsInNum) {
                val bit = num[i] - '0'
                if (i >= numOnes.size) {
                    numOnes += bit
                }
                numOnes[i] += bit
            }
        }

        var gammaRate = 0
        var epsilonRate = 0
        for (i in numOnes.lastIndex downTo 0) {
            // most common bit at index i
            val mcb = if (numOnes[numOnes.lastIndex - i] > input.size / 2) 1 else 0
            // least common bit at index i
            val lcb = (mcb + 1) % 2

            gammaRate = gammaRate or (mcb shl i)
            epsilonRate = epsilonRate or (lcb shl i)
        }

        // answer = 2648450
        return gammaRate * epsilonRate
    }

    fun part2(input: List<String>): Int {
        fun chooseBitAtIndex(nums: List<String>, idx: Int, criteria: (numOnes: Int, numZeros: Int) -> Int): Int {
            val numOnes =  nums.count { num -> num[idx] == '1' }
            val numZeros = nums.size - numOnes
            return criteria(numOnes, numZeros)
        }

        fun filterByBitAtIndex(nums: List<String>, bit: Int, idx: Int): List<String> =
            nums.filter { num -> num[idx] == '0' + bit }

        fun rating(nums: List<String>, criteria: (numOnes: Int, numZeros: Int) -> Int): Int {
            var remainingNums = nums
            var idx = 0
            while (remainingNums.size > 1) {
                val bit = chooseBitAtIndex(remainingNums, idx, criteria)
                remainingNums = filterByBitAtIndex(remainingNums, bit, idx)
                idx++
            }
            return remainingNums[0].toInt(radix = 2)
        }

        val oxygenGeneratorRating = rating(input) { numOnes, numZeros -> if (numOnes >= numZeros) 1 else 0 }
        val co2ScrubberRating = rating(input) { numOnes, numZeros -> if (numZeros <= numOnes) 0 else 1 }

        // answer = 2845944
        return oxygenGeneratorRating * co2ScrubberRating
    }

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
