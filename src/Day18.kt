import java.util.*

sealed class TreeNode(var parent: PairNode?)

class PairNode(var left: TreeNode, var right: TreeNode, parent: PairNode? = null) : TreeNode(parent) {

    init {
        left.parent = this
        right.parent = this
    }

    override fun toString(): String = "[$left,$right]"
}

class IntNode(var value: Int, parent: PairNode? = null) : TreeNode(parent) {

    override fun toString(): String = value.toString()
}

class SnailfishNumber(private val root: PairNode) {

    init {
        reduce()
    }

    operator fun plus(that: SnailfishNumber): SnailfishNumber {
        val sum = PairNode(this.root, that.root).also {
            this.root.parent = it
            that.root.parent = it
        }
        return SnailfishNumber(sum)
    }

    fun copy(): SnailfishNumber {
        fun copyOf(node: TreeNode): TreeNode = when (node) {
            is IntNode -> IntNode(node.value)
            is PairNode -> PairNode(copyOf(node.left), copyOf(node.right))
        }
        return SnailfishNumber(copyOf(root) as PairNode)
    }

    fun magnitude(): Int {
        fun magnitude(node: TreeNode): Int = when (node) {
            is IntNode -> node.value
            is PairNode -> 3 * magnitude(node.left) + 2 * magnitude(node.right)
        }
        return magnitude(root)
    }

    private fun reduce() {
        // explode nested pair
        findLeftmostPairToExplode(root)?.also { pair ->
            explode(pair)
            reduce()
        }

        // split regular number
        findLeftmostNumberToSplit(root)?.also { num ->
            split(num)
            reduce()
        }
    }

    // DFS: preorder traversal to nested pairs of depth = maxDepth
    private fun findLeftmostPairToExplode(root: PairNode, depth: Int = 4): PairNode? {
        if (depth == 0) {
            return root
        }

        var ans: PairNode? = null
        if (root.left is PairNode) {
            ans = findLeftmostPairToExplode(root.left as PairNode, depth - 1)
        }

        if (ans == null && root.right is PairNode) {
            ans = findLeftmostPairToExplode(root.right as PairNode, depth - 1)
        }
        return ans
    }

    private fun explode(pair: PairNode) {
        findFirstLeftNumber(pair)?.also { it.value += (pair.left as IntNode).value }
        findFirstRightNumber(pair)?.also { it.value += (pair.right as IntNode).value }

        val parent = checkNotNull(pair.parent)
        val num = IntNode(0, parent)
        when (parent.left) {
            pair -> parent.left = num
            else -> parent.right = num
        }
    }

    private fun findFirstLeftNumber(node: PairNode): IntNode? {
        var curr = node
        var parent = node.parent
        while (parent != null) {
            if (parent.left is IntNode) {
                return parent.left as IntNode
            }
            if (parent.left is PairNode && parent.left != curr) {
                // find the rightmost IntNode going down
                var right = parent.left
                while (right is PairNode) {
                    right = right.right
                }
                return right as IntNode
            }
            curr = parent
            parent = parent.parent
        }
        return null
    }

    private fun findFirstRightNumber(node: PairNode): IntNode? {
        var curr = node
        var parent = node.parent
        while (parent != null) {
            if (parent.right is IntNode) {
                return parent.right as IntNode
            }
            if (parent.right is PairNode && parent.right != curr) {
                // find the leftmost IntNode going down
                var left = parent.right
                while (left is PairNode) {
                    left = left.left
                }
                return left as IntNode
            }
            curr = parent
            parent = parent.parent
        }
        return null
    }

    // DFS: preorder traversal to find the leftmost regular number >= 10
    private fun findLeftmostNumberToSplit(root: TreeNode): IntNode? = when (root) {
        is IntNode -> if (root.value >= 10) root else null
        is PairNode -> findLeftmostNumberToSplit(root.left) ?: findLeftmostNumberToSplit(root.right)
    }

    private fun split(num: IntNode) {
        val parent = checkNotNull(num.parent)
        val pair = PairNode(IntNode(num.value / 2), IntNode(num.value - num.value / 2), parent)
        when (parent.left) {
            num -> parent.left = pair
            else -> parent.right = pair
        }
    }

    override fun toString(): String = root.toString()

    companion object {
        fun parse(s: String): SnailfishNumber {
            val stack = Stack<Any>()
            for (c in s) {
                when {
                    c == '[' || c == ',' -> stack.push(c)
                    c.isDigit() -> {
                        val digit = c.digitToInt()
                        if (stack.isEmpty() || stack.peek() !is IntNode) {
                            stack.push(IntNode(digit))
                        } else {
                            val top = stack.peek() as IntNode
                            top.value *= 10
                            top.value += digit
                        }
                    }
                    c == ']' -> {
                        val right = stack.pop() as TreeNode
                        check(stack.pop() == ',')
                        val left = stack.pop() as TreeNode
                        check(stack.pop() == '[')
                        stack.push(PairNode(left, right))
                    }
                }
            }
            val root = stack.pop() as PairNode
            return SnailfishNumber(root)
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        println("# Part 1")
        var sum = SnailfishNumber.parse(input[0])
        for (line in input.asSequence().drop(1)) {
            sum += SnailfishNumber.parse(line)
            println("Sum = $sum")
        }
        return sum.magnitude()
    }

    fun part2(input: List<String>): Int {
        println("# Part 2")
        val nums = input.map { line -> SnailfishNumber.parse(line) }
        var maxMagnitude = -1
        for (i in nums.indices) {
            for (j in nums.indices) {
                if (i != j) {
                    println("x     = ${nums[i]}")
                    println("y     = ${nums[j]}")

                    val sum = nums[i].copy() + nums[j].copy()
                    println("x + y = $sum")

                    val magnitude = sum.magnitude()
                    maxMagnitude = maxOf(maxMagnitude, magnitude)

                    println("Current magnitude = $magnitude, max magnitude = $maxMagnitude")
                    println("---")
                }
            }
        }
        return maxMagnitude
    }

    println("*** Tests ***")
    check(part1(readInput("Day18_test")) == 4140)
    check(part2(readInput("Day18_test")) == 3993)

    println("\n*** Answers ***")
    val input = readInput("Day18")
    println(part1(input)) // answer = 3216
    println(part2(input)) // answer = 4643
}
