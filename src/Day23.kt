import kotlin.math.abs

enum class Label(val destRoomIndex: Int, val costOfMove: Int) {
    A(destRoomIndex = 2, costOfMove = 1),
    B(destRoomIndex = 4, costOfMove = 10),
    C(destRoomIndex = 6, costOfMove = 100),
    D(destRoomIndex = 8, costOfMove = 1000);

    companion object {
        val destRoomIndices: Set<Int> = values().asSequence().map { it.destRoomIndex }.toSet()
    }
}

data class AmphipodState(
    val rooms: Map<Label, List<Label?>>,
    val hallway: List<Label?>
) {
    class Move(val cost: Int, val nextState: AmphipodState)

    private class MutableView(
        val rooms: MutableMap<Label, MutableList<Label?>>,
        val hallway: MutableList<Label?>
    )

    private constructor(view: MutableView) : this(view.rooms, view.hallway)

    init {
        // there are exactly 4 rooms of the same size
        check(rooms.keys.size == 4)
        val rooms = rooms.values.asSequence()
        val size = rooms.first().size
        check(rooms.drop(1).all { it.size == size })
    }

    val isTarget: Boolean = rooms.entries.all { (label, room) -> room.all { it == label } }

    fun canMoveFromHallwayIntoDestRoom(hallwayIndex: Int): Boolean {
        val amphipod = hallway[hallwayIndex] ?: return false
        val destRoom = rooms[amphipod]!!
        return destRoom.all { it == null || it == amphipod } && pathIsNotBlocked(amphipod, hallwayIndex)
    }

    fun moveFromHallwayIntoDestRoom(hallwayIndex: Int): Move {
        val amphipod = hallway[hallwayIndex]!!
        val indexInRoomToMoveInto = rooms[amphipod]!!.indexOfLast { it == null }.also { check(it != -1) }

        // form a new state
        val copy = mutableCopy().apply {
            rooms[amphipod]!![indexInRoomToMoveInto] = amphipod
            hallway[hallwayIndex] = null
        }

        val cost = costOfMove(amphipod, hallwayIndex, amphipod.destRoomIndex, indexInRoomToMoveInto)
        println("++ Moved $amphipod from hallway index = $hallwayIndex into the destination room, cost = $cost")
        return Move(cost, AmphipodState(copy))
    }

    fun canMoveFromRoomAtAll(room: Label): Boolean {
        fun cannotMoveFromRoomAtAll(room: Label): Boolean {
            val sourceRoom = rooms[room]!!
            return sourceRoom.all { it == null || it == room }
        }
        return !cannotMoveFromRoomAtAll(room)
    }

    fun canMoveFromRoomIntoHallway(room: Label, hallwayIndex: Int): Boolean =
        (hallway[hallwayIndex] == null) && (hallwayIndex !in Label.destRoomIndices) && pathIsNotBlocked(room, hallwayIndex)

    fun moveFromRoomIntoHallway(room: Label, hallwayIndex: Int): Move {
        val sourceRoom = rooms[room]!!
        val indexInRoomToMoveFrom = sourceRoom.indexOfFirst { it != null }.also { check(it != -1) }
        val amphipod = sourceRoom[indexInRoomToMoveFrom]!!

        // form a new state
        val copy = mutableCopy().apply {
            rooms[room]!![indexInRoomToMoveFrom] = null
            hallway[hallwayIndex] = amphipod
        }

        val cost = costOfMove(amphipod, hallwayIndex, room.destRoomIndex, indexInRoomToMoveFrom)
        println("-- Moved $amphipod from room = $room into hallway index = $hallwayIndex, cost = $cost")
        return Move(cost, AmphipodState(copy))
    }

    private fun pathIsNotBlocked(room: Label, hallwayIndex: Int): Boolean {
        val range = when {
            room.destRoomIndex < hallwayIndex -> room.destRoomIndex until hallwayIndex
            hallwayIndex < room.destRoomIndex -> hallwayIndex + 1..room.destRoomIndex
            else -> return false
        }
        return range.all { i -> hallway[i] == null }
    }

    private fun mutableCopy(): MutableView {
        val copyOfRooms = rooms.mapValuesTo(mutableMapOf()) { (_, room) -> room.toMutableList() }
        val copyOfHallway = hallway.toMutableList()
        return MutableView(copyOfRooms, copyOfHallway)
    }

    private fun costOfMove(amphipod: Label, hallwayIndex: Int, destRoomIndex: Int, indexInRoom: Int): Int {
        check(destRoomIndex in Label.destRoomIndices)
        val numMoves = abs(hallwayIndex - destRoomIndex) + indexInRoom + 1
        return amphipod.costOfMove * numMoves
    }

    //#############
    //#...........#
    //###B#C#B#D###
    //  #A#D#C#A#
    //  #########
    override fun toString(): String = buildString {
        append("#############\n")
        append("#${hallway.joinToString(separator = "", transform = { label -> label?.name ?: "." })}#\n")
        repeat(rooms[Label.A]!!.size) { i ->
            val line = buildString {
                val margin = if (i == 0) "##" else "  "
                append(margin)
                for ((_, room) in rooms) {
                    append("#")
                    append(room[i] ?: '.')
                }
                append("#$margin")
            }
            append("$line\n")
        }
        append("  #########  \n")
    }
}

fun main() {
    fun readInitialState(input: List<String>): AmphipodState {
        val rooms = mutableMapOf<Label, MutableList<Label?>>()
        for (row in 2 until input.lastIndex) {
            for (label in Label.values()) {
                val c = input[row][label.destRoomIndex + 1]
                rooms.getOrPut(label) { mutableListOf() } += enumValueOf<Label>(c.toString())
            }
        }
        return AmphipodState(rooms, arrayOfNulls<Label>(11).toList())
    }

    fun minCostToOrganize(state: AmphipodState, dp: MutableMap<AmphipodState, Int>): Int {
        println(state)

        if (state.isTarget) {
            return 0
        }

        if (state in dp) {
            return dp[state]!!
        }

        // if possible, move an amphipod from the hallway into the destination room
        for (hallwayIndex in state.hallway.indices) {
            if (state.canMoveFromHallwayIntoDestRoom(hallwayIndex)) {
                val move = state.moveFromHallwayIntoDestRoom(hallwayIndex)
                val futureCost = minCostToOrganize(move.nextState, dp)
                return if (futureCost == Int.MAX_VALUE) Int.MAX_VALUE else move.cost + futureCost
            }
        }

        // otherwise, move amphipods from disorganized rooms into the hallway
        var minCost = Int.MAX_VALUE
        for (room in Label.values()) {
            if (!state.canMoveFromRoomAtAll(room)) {
                continue
            }
            for (hallwayIndex in state.hallway.indices) {
                if (state.canMoveFromRoomIntoHallway(room, hallwayIndex)) {
                    val move = state.moveFromRoomIntoHallway(room, hallwayIndex)
                    val futureCost = minCostToOrganize(move.nextState, dp)
                    minCost = minOf(minCost, if (futureCost == Int.MAX_VALUE) Int.MAX_VALUE else move.cost + futureCost)
                }
            }
        }

        dp[state] = minCost
        return minCost
    }

    fun solve(input: List<String>): Int {
        val initial = readInitialState(input)
        return minCostToOrganize(initial, mutableMapOf())
    }

    fun part1(input: List<String>): Int = solve(input)

    fun part2(input: List<String>): Int = solve(input)

    println("*** Test: Part 1 ***")
    check(part1(readInput("Day23_test_part1")) == 12521)

    println("\n*** Test: Part 2 ***")
    check(part2(readInput("Day23_test_part2")) == 44169)

    println("\n*** Answer: Part 1 ***")
    println(part1(readInput("Day23_part1"))) // answer = 13336

    println("\n*** Answer: Part 2 ***")
    println(part2(readInput("Day23_part2"))) // answer = 53308
}
