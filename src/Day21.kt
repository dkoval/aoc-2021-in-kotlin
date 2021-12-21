private const val WINNING_SCORE_PART1 = 1000
private const val WINNING_SCORE_PART2 = 21

fun main() {
    data class Player(val position: Int, val score: Int) {

        fun move(numSteps: Int): Player {
            val nextPosition = (position + numSteps - 1) % 10 + 1
            return Player(nextPosition, score + nextPosition)
        }
    }

    fun part1(input: List<String>): Int {
        class DeterministicDice {
            private var next = 1
            private var _numRolls = 0

            val numRolls: Int get() = _numRolls

            fun roll(times: Int = 1): Int {
                var sum = 0
                repeat(times) {
                    sum += ((next - 1) % 100 + 1).also { next = it + 1 }
                }
                return sum.also { _numRolls += times }
            }
        }

        val (pos1, pos2) = input.asSequence()
            .map { it.replace("""Player \d{1,2} starting position: """.toRegex(), "") }
            .map { it.toInt() }
            .toList()

        var player1 = Player(pos1, 0)
        var player2 = Player(pos2, 0)
        val dice = DeterministicDice()

        while (true) {
            // Player 1 moves
            player1 = player1.move(numSteps = dice.roll(times = 3))
            if (player1.score >= WINNING_SCORE_PART1) {
                return player2.score * dice.numRolls
            }

            // Player 2 moves
            player2 = player2.move(numSteps = dice.roll(times = 3))
            if (player2.score >= WINNING_SCORE_PART1) {
                return player1.score * dice.numRolls
            }
        }
    }

    fun part2(input: List<String>): Long {
        class Wins(var player1: Long, var player2: Long) {

            fun max(): Long = maxOf(player1, player2)
        }

        data class Key(val activePlayer: Player, val waitingPlayer: Player)

        fun countWins(activePlayer: Player, waitingPlayer: Player, memo: MutableMap<Key, Wins>): Wins {
            // Base case #1
            if (activePlayer.score >= WINNING_SCORE_PART2) {
                // Player 1 wins
                return Wins(1, 0)
            }

            // Base case #2
            if (waitingPlayer.score >= WINNING_SCORE_PART2) {
                // Player 2 wins
                return Wins(0, 1)
            }

            // Already solved?
            val key = Key(activePlayer, waitingPlayer)
            if (key in memo) {
                return memo[key]!!
            }

            // Generate all possible combinations of 3 subsequent rolls; each roll results in 1, 2 or 3.
            val ans = Wins(0, 0)
            for (r1 in 1..3) {
                for (r2 in 1..3) {
                    for (r3 in 1..3) {
                        val numSteps = r1 + r2 + r3

                        // In the next iteration, it's Player 2 move
                        val wins = countWins(waitingPlayer, activePlayer.move(numSteps), memo)

                        ans.player1 += wins.player2
                        ans.player2 += wins.player1
                    }
                }
            }

            memo[key] = ans
            return ans
        }

        val (pos1, pos2) = input.asSequence()
            .map { it.replace("""Player \d{1,2} starting position: """.toRegex(), "") }
            .map { it.toInt() }
            .toList()

        return countWins(Player(pos1, 0), Player(pos2, 0), mutableMapOf()).max()
    }

    check(part1(readInput("Day21_test")) == 739785)
    check(part2(readInput("Day21_test")) == 444356092776315)

    val input = readInput("Day21")
    println(part1(input)) // answer = 757770
    println(part2(input)) // answer = 712381680443927
}
