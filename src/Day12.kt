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

    fun dfs1(
        adj: Map<String, List<String>>,
        u: String,
        target: String,
        visited: MutableSet<String>,
        currPath: MutableList<String>,
        distinctPaths: MutableList<List<String>>
    ) {
        if (u == target) {
            val copy = currPath.toMutableList()
            copy += target
            distinctPaths += copy
            return
        }

        if (u in visited) {
            return
        }

        // mark the current vertex as visited
        if (u[0].isLowerCase()) {
            visited += u
        }

        // run DFS for vertices adjacent to u
        currPath += u
        for (v in adj[u]!!) {
            dfs1(adj, v, target, visited, currPath, distinctPaths)
        }

        // backtrack to find more paths
        if (u[0].isLowerCase()) {
            visited -= u
        }
        currPath.removeAt(currPath.lastIndex)
    }

    fun part1(input: List<String>): Int {
        val adj = adj(input)
        val distinctPaths = mutableListOf<List<String>>()
        dfs1(adj, start, end, mutableSetOf(), mutableListOf(), distinctPaths)
        return distinctPaths.size
    }

    fun dfs2(
        adj: Map<String, List<String>>,
        u: String,
        target: String,
        visited: MutableMap<String, Int>,
        currPath: MutableList<String>,
        distinctPaths: MutableList<List<String>>
    ) {
        if (u == target) {
            val copy = currPath.toMutableList()
            copy += target
            distinctPaths += copy
            return
        }

        if (u in visited && (u == start || visited[u] == 2 || currPath.any { x -> visited[x] == 2 })) {
            return
        }

        // mark the current vertex as visited
        if (u[0].isLowerCase()) {
            visited[u] = (visited[u] ?: 0) + 1
        }

        // run DFS for vertices adjacent to u
        currPath += u
        for (v in adj[u]!!) {
            dfs2(adj, v, target, visited, currPath, distinctPaths)
        }

        // backtrack to find more paths
        if (u[0].isLowerCase()) {
            val count = visited[u]!!
            if (count == 1) {
                visited -= u
            } else {
                visited[u] = count - 1
            }
        }
        currPath.removeAt(currPath.lastIndex)
    }

    fun part2(input: List<String>): Int {
        val adj = adj(input)
        val distinctPaths = mutableListOf<List<String>>()
        dfs2(adj, start, end, mutableMapOf(), mutableListOf(), distinctPaths)
        return distinctPaths.size
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
