const val UP = '^'
const val DOWN = 'v'
const val LEFT = '<'
const val RIGHT = '>'

fun day24(lines: List<String>) {
    val map = findWalls(lines)
    val blizzards = findBlizzards(lines)
    val start = ValleyPos(map[0].indexOfFirst { it == '.' }, 0)
    val goal = ValleyPos(map.last().indexOfFirst { it == '.'}, map.indices.last)
    val possiblePositions = mutableSetOf(start)
    
    var tripDuration = moveToTarget(map, possiblePositions, blizzards, goal)
    println("Day 24 part 1: $tripDuration")
    
    possiblePositions.clear()
    possiblePositions.add(goal)
    tripDuration += moveToTarget(map, possiblePositions, blizzards, start)
    
    possiblePositions.clear()
    possiblePositions.add(start)
    tripDuration += moveToTarget(map, possiblePositions, blizzards, goal)

    println("Day 24 part 2: $tripDuration")
}

fun moveToTarget(map: Array<CharArray>, possiblePositions: MutableSet<ValleyPos>, blizzards: List<Blizzard>, target: ValleyPos): Int {
    for (i in 1..500) {
        updateBlizzardPositions(blizzards, map[0].size - 1, map.size - 1)
        updateMap(blizzards, map)
        moveToAllOpenTiles(map, possiblePositions)

        if (possiblePositions.contains(target)) {
            return i
        }
    }
    
    return -1
}

fun moveToAllOpenTiles(map: Array<CharArray>, possiblePositions: MutableSet<ValleyPos>) {
    val nextPossiblePositions = mutableSetOf<ValleyPos>()
    
    possiblePositions.forEach { 
        if (map[it.y][it.x] == '.') {
            nextPossiblePositions.add(ValleyPos(it.x, it.y))
        }

        if (it.y > 0 && map[it.y - 1][it.x] == '.') {
            nextPossiblePositions.add(ValleyPos(it.x, it.y - 1))
        }

        if (it.y < map.indices.last && map[it.y + 1][it.x] == '.') {
            nextPossiblePositions.add(ValleyPos(it.x, it.y + 1))
        }

        if (map[it.y][it.x -1 ] == '.') {
            nextPossiblePositions.add(ValleyPos(it.x - 1, it.y))
        }

        if (map[it.y][it.x + 1] == '.') {
            nextPossiblePositions.add(ValleyPos(it.x + 1, it.y))
        }
    }
    
    possiblePositions.clear()
    possiblePositions.addAll(nextPossiblePositions)
}

fun updateMap(blizzards: List<Blizzard>, map: Array<CharArray>) {
    for (y in 1 until map.indices.last) {
        for (x in 1 until map[0].indices.last) {
            map[y][x] = '.'
        }
    }
    
    blizzards.forEach { 
        map[it.valleyPos.y][it.valleyPos.x] = '#'
    }
}

fun updateBlizzardPositions(blizzards: List<Blizzard>, maxX: Int, maxY: Int) {
    blizzards.forEach {
        when (it.dir) {
            UP -> {
                it.valleyPos.y--
                if (it.valleyPos.y == 0) {
                    it.valleyPos.y = maxY - 1
                }
            }
            DOWN -> {
                it.valleyPos.y++
                if (it.valleyPos.y == maxY) {
                    it.valleyPos.y = 1
                }
            }
            LEFT -> {
                it.valleyPos.x--
                if (it.valleyPos.x == 0) {
                    it.valleyPos.x = maxX - 1
                }
            }
            RIGHT -> {
                it.valleyPos.x++
                if (it.valleyPos.x == maxX) {
                    it.valleyPos.x = 1
                }
            }
        }
    }
}

fun findWalls(lines: List<String>): Array<CharArray> {
    val world = Array(lines.size) { CharArray(lines[0].length) { '.' } }

    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, tile ->
            if (tile == '#') {
                world[y][x] = '#'
            }
        }
    }

    return world
}

fun findBlizzards(lines: List<String>): MutableList<Blizzard> {
    val blizzards = mutableListOf<Blizzard>()

    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, tile ->
            if (setOf(UP, DOWN, LEFT, RIGHT).contains(tile)) {
                blizzards.add(Blizzard(ValleyPos(x, y), tile))
            }
        }
    }

    return blizzards
}

data class Blizzard(val valleyPos: ValleyPos, val dir: Char)

data class ValleyPos(var x: Int, var y: Int)