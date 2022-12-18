import java.util.*
import kotlin.math.abs

fun day18(lines: List<String>) {
    val cubes = getCubes(lines)
    val exposedSidesCount = findExposedSidesCount(cubes)
    
    val potentialAirPockets = findPotentialAirPockets(cubes)
    
    val airPocketGraph = buildAirPocketGraph(potentialAirPockets)
    val reachableAirPockets = findReachableAirPocketsCount(airPocketGraph)
    
    val airPocketExposedSidesCount = findAirPocketExposedSidesCount(cubes, potentialAirPockets.filterNot { reachableAirPockets.contains(it) })
    
    println("Day 18 part 1: $exposedSidesCount")
    println("Day 18 part 2: ${exposedSidesCount - airPocketExposedSidesCount }")
}

fun findAirPocketExposedSidesCount(cubes: List<Cube>, airPockets: List<Cube>): Int {
    var totalSidesExposed = 0

    for (i in cubes.indices) {
        var sidesExposed = 0
        val cube = cubes[i]
        for (j in airPockets.indices) {
            val airPocket = airPockets[j]
            if (areAdjacent(cube, airPocket)) {
                sidesExposed++
            }
        }
        totalSidesExposed += sidesExposed
    }

    return totalSidesExposed
}

fun findReachableAirPocketsCount(airPocketGraph: MutableMap<Cube, List<Cube>>): List<Cube> {
    val queue: Queue<Cube> = LinkedList()
    val visited = mutableSetOf<Cube>()
    
    queue.add(airPocketGraph.keys.first())
    
    while (queue.isNotEmpty()) {
        val current = queue.poll()
        
        val nextNodes = airPocketGraph[current]!!
            .filterNot { visited.contains(it) }
            //.filterNot { queue.contains(it) }
        
        nextNodes.forEach { 
            visited.add(it)
            queue.add(it)
        }
    }
    
    return visited.toList()
}

fun buildAirPocketGraph(potentialAirPockets: List<Cube>): MutableMap<Cube, List<Cube>> {
    val graph = mutableMapOf<Cube, List<Cube>>()
    for (i in potentialAirPockets.indices) {
        val airPocket1 = potentialAirPockets[i]
        val neighbours = mutableListOf<Cube>()       
        for (j in potentialAirPockets.indices) {
            if (i == j) {
                continue
            }
            val airPocket2 = potentialAirPockets[j]
            if (areAdjacent(airPocket1, airPocket2)) {
                neighbours.add(airPocket2)
            }
        }
        graph[airPocket1] = neighbours
    }
    
    return graph
}

fun findPotentialAirPockets(cubes: List<Cube>): List<Cube> {
    val minX = cubes.minOf { it.x } - 1
    val maxX = cubes.maxOf { it.x } + 1
    val minY = cubes.minOf { it.y } - 1
    val maxY = cubes.maxOf { it.y } + 1
    val minZ = cubes.minOf { it.z } - 1
    val maxZ = cubes.maxOf { it.z } + 1
    
    val potentialAirPockets = mutableListOf<Cube>()
    
    for (x in minX..maxX) {
        for (y in minY..maxY) {
            for (z in minZ..maxZ) {
                potentialAirPockets.add(Cube(x, y, z))
            }
        }
    }
    
    return potentialAirPockets.filterNot { 
        cubes.contains(it)
    }
}

fun findExposedSidesCount(cubes: List<Cube>): Int {
    var totalSidesExposed = 0

    for (i in cubes.indices) {
        var sidesExposed = 6
        val cube1 = cubes[i]
        for (j in cubes.indices) {
            if (i == j) {
                continue
            }
            val cube2 = cubes[j]
            if (areAdjacent(cube1, cube2)) {
                sidesExposed--
            }
        }
        totalSidesExposed += sidesExposed
    }
    
    return totalSidesExposed
}

fun areAdjacent(c1: Cube, c2: Cube): Boolean {
    if (c1.x == c2.x && c1.y == c2.y && abs(c1.z - c2.z) == 1) {
        return true
    }
    
    if (c1.x == c2.x && c1.z == c2.z && abs(c1.y - c2.y) == 1) {
        return true
    }
    
    if (c1.y == c2.y && c1.z == c2.z && abs(c1.x - c2.x) == 1) {
        return true
    }
    
    return false
}

fun getCubes(lines: List<String>): List<Cube> {
    val cubes = mutableListOf<Cube>()

    lines.forEach {
        cubes.add(Cube(
            Integer.parseInt(it.split(",")[0]),
            Integer.parseInt(it.split(",")[1]),
            Integer.parseInt(it.split(",")[2]),
        ))
    }

    return cubes
}

data class Cube (val x: Int, val y: Int, val z: Int)