import java.util.*

fun day12(lines: List<String>) {
    val heightMap = getHeightMap(lines)
    val coordinates = findStartAndEndCoordinates(lines)

    var steps = findShortestPath(heightMap, coordinates[0], coordinates[1])
    println("Day 12 part 1: $steps")
    
    val startCandidates = findStartCandidates(heightMap)
    steps = findShortestShortestPath(heightMap, startCandidates, coordinates[1])
    println("Day 12 part 2: $steps")
}

fun getHeightMap(lines: List<String>): Array<IntArray>  {
    val heightMap = Array(lines.size) { IntArray(lines[0].length) { 0 } }

    lines.forEachIndexed { i, row ->
        row.forEachIndexed { j, _ ->
            heightMap[i][j] = getHeight(row[j])
        }
    }
    
    return heightMap
}

fun findStartAndEndCoordinates(lines: List<String>): List<Pair<Int, Int>> {
    var start: Pair<Int, Int> = Pair(0,0)
    var end: Pair<Int, Int> = Pair(0,0)
    
    lines.forEachIndexed { i, row ->
        row.forEachIndexed { j, _ ->
            if (row[j] == 'S') {
                start = Pair(i ,j)
            } else if (row[j] == 'E') {
                end = Pair(i, j)
            }
        }
    }
    
    return listOf(start, end)
}

fun getHeight(heightChar: Char): Int {
    return when (heightChar) {
        'S' -> 'a'.code
        'E' -> 'z'.code
        else -> heightChar.code
    }
}

fun findShortestShortestPath(heightMap: Array<IntArray>, startCandidates: List<Pair<Int, Int>>, end: Pair<Int, Int>): Int {
    var shortestPathSoFar = Int.MAX_VALUE
    
    startCandidates.forEach { 
        val currentShortestPath = findShortestPath(heightMap, it, end)
        if (currentShortestPath < shortestPathSoFar) {
            shortestPathSoFar = currentShortestPath
        }
    }
    
    return shortestPathSoFar;
}

fun findStartCandidates(heightMap: Array<IntArray>): List<Pair<Int, Int>> {
    val startCandidates = mutableListOf<Pair<Int, Int>>()
    
    heightMap.forEachIndexed { i, row ->
        row.forEachIndexed { j, _ ->
            if (row[j] == 97) {
                startCandidates.add(Pair(i, j))
            }
        }
    }
    
    return startCandidates
}

fun findShortestPath(heightMap: Array<IntArray>, start: Pair<Int, Int>, end: Pair<Int, Int>): Int {
    val dx = arrayOf(-1, 0, 1, 0)
    val dy = arrayOf(0, 1, 0, -1)
    val xSize = heightMap.size
    val ySize = heightMap[0].size
    val distance = Array(xSize) { IntArray(ySize) { Int.MAX_VALUE } }

    distance[start.first][start.second] = 0

    val pq: PriorityQueue<Cell> = PriorityQueue(xSize * ySize, compareBy { it.distance })

    pq.add(Cell(start.first, start.second, distance[start.first][start.second]))

    while (pq.isNotEmpty()) {
        val current = pq.poll()

        for (i in 0..3) {
            val rows = current.x + dx[i]
            val cols = current.y + dy[i]

            if (isInGrid(rows, cols, xSize, ySize) && possibleDirection(rows, cols, current, heightMap)) {
                if (distance[rows][cols] > distance[current.x][current.y] + 1) {

                    if (distance[rows][cols] != Int.MAX_VALUE) {
                        val adj = Cell(rows, cols, distance[rows][cols])
                        pq.remove(adj)
                    }

                    // Insert cell with updated distance
                    distance[rows][cols] = distance[current.x][current.y] + 1
                    pq.add(Cell(rows, cols, distance[rows][cols]))
                }
            }
        }
    }
    
    return distance[end.first][end.second]
}

fun possibleDirection(rows: Int, cols: Int, current: Cell, heightMap: Array<IntArray>): Boolean {
    return heightMap[current.x][current.y] - heightMap[rows][cols] >= -1
}

fun isInGrid(i: Int, j: Int, xSize: Int, ySize: Int): Boolean {
    return i in 0 until xSize && j in 0 until ySize
}

data class Cell (val x: Int, val y: Int, val distance: Int)