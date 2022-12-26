fun day23(lines: List<String>) {
    val elfPositions = parseElfPositions(lines)
    val directionOrder = mutableListOf(Direction(0, -1), Direction(0, 1), Direction(-1, 0), Direction(1,0))

    /*val roundsNeeded = simulateRounds(elfPositions, directionOrder)

    println("Day 23 part 2: $roundsNeeded")*/
}

fun findEmptyGroundTiles(elfPositions: Map<Int, ElfPosition>): Int {
    val minX = elfPositions.values.minBy { it.x }.x
    val maxX = elfPositions.values.maxBy { it.x }.x
    val minY = elfPositions.values.minBy { it.y }.y
    val maxY = elfPositions.values.maxBy { it.y }.y

    return (maxX - minX + 1) * (maxY - minY + 1) - elfPositions.size
}

fun simulateRounds(elfPositions: Map<Int, ElfPosition>, directionOrder: MutableList<Direction>): Long {
    var i = 1L
    while (true) {
        val proposedPositions = mutableListOf<Pair<Int, ElfPosition>>()

        elfPositions.forEach {
            val nextStep = decideNextStep(it.value, it.key, elfPositions, directionOrder)
            if (nextStep != null) {
                proposedPositions.add(nextStep)
            }
        }

        removeDuplicateMoves(proposedPositions)
        performMoves(elfPositions, proposedPositions)
        if (proposedPositions.isNotEmpty()) {
            updateDirectionOrder(directionOrder)
        } else {
            return i
        }

        if (i == 10L) {
            println("Day 23 part 1: ${findEmptyGroundTiles(elfPositions)}")
        }

        i++
    }
}

fun decideNextStep(elf: ElfPosition, id: Int, elfPositions: Map<Int, ElfPosition>, directionOrder: List<Direction>): Pair<Int, ElfPosition>? {
    val occupiedDirections = mutableSetOf<Direction>()

    elfPositions.values.forEach {
        if (elf.y - 1 == it.y && it.x in elf.x-1..elf.x+1) {  //NORTH
            occupiedDirections.add(Direction(0, -1))
        }
        if (elf.y + 1 == it.y && it.x in elf.x-1..elf.x+1) { //SOUTH
            occupiedDirections.add(Direction(0, 1))
        }
        if (elf.x - 1 == it.x && it.y in elf.y-1..elf.y+1) { //WEST
            occupiedDirections.add(Direction(-1, 0))
        }
        if (elf.x + 1 == it.x && it.y in elf.y-1..elf.y+1) { // EAST
            occupiedDirections.add(Direction(1,0))
        }
    }

    if (occupiedDirections.isEmpty()) {
        return null
    }

    directionOrder.forEach {
        if (!occupiedDirections.contains(it)) {
            return Pair(id, ElfPosition(elf.x + it.x, elf.y + it.y))
        }
    }

    return null
}

fun removeDuplicateMoves(proposedPositions: MutableList<Pair<Int, ElfPosition>>) {
    val indicesToRemove = mutableSetOf<Int>()

    proposedPositions.forEachIndexed { index, pair ->
        proposedPositions.forEachIndexed { innerIndex, innerPair ->
            if (index != innerIndex) {
                if (pair.second == innerPair.second) {
                    indicesToRemove.add(index)
                    indicesToRemove.add(innerIndex)
                }
            }
        }
    }

    indicesToRemove.sortedDescending().forEach {
        proposedPositions.removeAt(it)
    }
}

fun performMoves(elfPositions: Map<Int, ElfPosition>, proposedPositions: List<Pair<Int, ElfPosition>>) {
    proposedPositions.forEach {
        elfPositions[it.first]!!.x = it.second.x
        elfPositions[it.first]!!.y = it.second.y
    }
}

fun updateDirectionOrder(directions: MutableList<Direction>) {
    val first = directions.removeFirst()
    directions.add(first)
}

fun parseElfPositions(lines: List<String>): Map<Int, ElfPosition> {
    val elfPositions = mutableMapOf<Int, ElfPosition>()

    var id = 0

    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, it ->
            if (it == '#') {
                elfPositions[id] = (ElfPosition(x, y))
                id++
            }
        }
    }

    return elfPositions
}

data class ElfPosition(var x: Int, var y: Int)

data class Direction(val x: Int, val y: Int)
