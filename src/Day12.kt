fun main() {
    val start = "start"
    val end = "end"

    fun adj(input: List<String>): Map<String, List<String>> {
        val adj = mutableMapOf<String, MutableList<String>>()
        for (line in input) {
            val (u, v) = line.split("-")
            adj.getOrPut(u) { mutableListOf() } += v
            adj.getOrPut(v) { mutableListOf() } += u
        }
        return adj
    }

    fun part1(input: List<String>): Int {
        fun dfs1(
            adj: Map<String, List<String>>,
            u: String,
            target: String,
            visited: MutableSet<String>
        ): Int {
            if (u == target) {
                return 1
            }

            if (u in visited) {
                return 0
            }

            // mark the current vertex as visited
            val smallCave = u[0].isLowerCase()
            if (smallCave) {
                visited += u
            }

            // having "appended" vertex u to the current path, run DFS for vertices adjacent to u
            var count = 0
            for (v in adj[u]!!) {
                count += dfs1(adj, v, target, visited)
            }

            // backtrack to find more paths
            if (smallCave) {
                visited -= u
            }
            return count
        }

        val adj = adj(input)
        return dfs1(adj, start, end, mutableSetOf())
    }

    fun part2(input: List<String>): Int {
        fun dfs2(
            adj: Map<String, List<String>>,
            u: String,
            target: String,
            visited: MutableMap<String, Int>
        ): Int {
            if (u == target) {
                return 1
            }

            if (u in visited && (u == start || visited[u] == 2 || visited.any { (_, times) -> times == 2 })) {
                return 0
            }

            // mark the current vertex as visited
            val smallCave = u[0].isLowerCase()
            if (smallCave) {
                visited[u] = (visited[u] ?: 0) + 1
            }

            // having "appended" vertex u to the current path, run DFS for vertices adjacent to u
            var count = 0
            for (v in adj[u]!!) {
                count += dfs2(adj, v, target, visited)
            }

            // backtrack to find more paths
            if (smallCave) {
                val times = visited[u]!!
                if (times > 1) {
                    visited[u] = times - 1
                } else {
                    visited -= u
                }
            }
            return count
        }

        val adj = adj(input)
        return dfs2(adj, start, end, mutableMapOf())
    }

    check(part1(readInput("Day12_test1")) == 10)
    check(part1(readInput("Day12_test2")) == 19)
    check(part1(readInput("Day12_test3")) == 226)

    check(part2(readInput("Day12_test1")) == 36)
    check(part2(readInput("Day12_test2")) == 103)
    check(part2(readInput("Day12_test3")) == 3509)

    val input = readInput("Day12")
    println(part1(input)) // answer = 4411
    println(part2(input)) // answer = 136767
}
