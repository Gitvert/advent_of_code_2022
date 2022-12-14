import kotlin.math.max

fun day14(lines: List<String>) {
    val paths = createPaths(lines)
    val bottomLessCave = createCave(paths)
    var sandAtRest = pourSand(bottomLessCave)
    
    println("Day 14 part 1: $sandAtRest")

    val caveWithFloor = createCave(paths)
    addFloor(caveWithFloor, paths)
    sandAtRest = pourSand(caveWithFloor) + 1

    println("Day 14 part 2: $sandAtRest")
}

fun pourSand(cave: Array<CharArray>): Int {
    var sandAtRest = true
    var sandAtRestCount = -1
    
    while (sandAtRest) {
        sandAtRestCount++
        sandAtRest = simulateSand(cave)
    }
    
    return sandAtRestCount
}

fun simulateSand(cave: Array<CharArray>): Boolean {
    val sand = Coordinate(0, 500)
    var sandMoved = true
    
    while (sandMoved) {
        sandMoved = false
        if (cave[sand.x + 1][sand.y] == '.') {
            sand.x++
            sandMoved = true
        } else if (cave[sand.x + 1][sand.y - 1] == '.') {
            sand.x++
            sand.y--
            sandMoved = true
        } else if (cave[sand.x + 1][sand.y + 1] == '.') {
            sand.x++
            sand.y++
            sandMoved = true
        }
        
        if (sand.x > 195 || sand.x == 0) {
            return false
        }
    }
    
    cave[sand.x][sand.y] = 'o'
    
    return true
}

fun addFloor(cave: Array<CharArray>, paths: List<Pair<Coordinate, Coordinate>>) {
    val floorPosition = paths.maxOfOrNull { max(it.first.x, it.second.x) }!! + 2
    
    for (i in 0 until 1000) {
        cave [floorPosition][i] = '#'
    }
}

fun createCave(paths: List<Pair<Coordinate, Coordinate>>): Array<CharArray> {
    val cave = Array(200) { CharArray(1000) { '.' } }
    
    paths.forEach { 
        val start = it.first
        val end = it.second
        
        if (start.x == end.x) {
            if (start.y < end.y) {
                for (i in start.y .. end.y) {
                    cave[start.x][i] = '#'
                }
            } else {
                for (i in end.y .. start.y) {
                    cave[start.x][i] = '#'
                }
            }
        } else if (start.y == end.y) {
            if (start.x < end.x) {
                for (i in start.x .. end.x) {
                    cave[i][start.y] = '#'
                }
            } else {
                for (i in end.x .. start.x) {
                    cave[i][start.y] = '#'
                }
            }
        }
    }
    
    return cave
}

fun createPaths(lines: List<String>): List<Pair<Coordinate, Coordinate>> {
    val paths = mutableListOf<Pair<Coordinate, Coordinate>>()
    lines.forEach { 
        val instructions = it.split(" -> ")
        
        for (i in 0 until instructions.size - 1) {
            val y1 = Integer.parseInt(instructions[i].split(",")[0])
            val x1 = Integer.parseInt(instructions[i].split(",")[1])

            val y2 = Integer.parseInt(instructions[i + 1].split(",")[0])
            val x2 = Integer.parseInt(instructions[i + 1].split(",")[1])
            
            paths.add(Pair(Coordinate(x1, y1), Coordinate(x2, y2)))
        }
    }
    
    return paths
}

data class Coordinate(var x: Int, var y: Int)