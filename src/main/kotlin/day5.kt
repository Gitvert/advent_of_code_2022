import java.util.*

fun day5(lines: List<String>) {
    val crateMap = getCrateMap(lines)
    val crateStacks9000 = generateCrateStacks(crateMap)
    val crateStacks9001 = generateCrateStacks(crateMap)
    val moves = getMoves(lines)
    
    performMoves9000(crateStacks9000, moves)
    performMoves9001(crateStacks9001, moves)

    println("Day 5 part 1: ${findTopCrates(crateStacks9000)}")
    println("Day 5 part 2: ${findTopCrates(crateStacks9001)}")
}

fun findTopCrates(crateStacks: List<Stack<Char>>): String {
    var topCrates = ""
    
    crateStacks.forEach { 
        topCrates += it.peek()
    }
    
    return topCrates
}

fun performMoves9000(crateStacks: List<Stack<Char>>, moves: List<Move>) {
    moves.forEach { 
        for (i in 0 until it.amount) {
            crateStacks[it.to].add(crateStacks[it.from].pop())
        }
    }
}

fun performMoves9001(crateStacks: List<Stack<Char>>, moves: List<Move>) {
    val tempStack = Stack<Char>()
    
    moves.forEach {
        tempStack.clear()
        for (i in 0 until it.amount) {
            tempStack.add(crateStacks[it.from].pop())
        }

        for (i in 0 until it.amount) {
            crateStacks[it.to].add(tempStack.pop())
        }
    }
}

fun getMoves(lines: List<String>): List<Move> {
    val moves = mutableListOf<Move>()
    
    lines
        .filter { it.startsWith("move") }
        .forEach { 
            val amount = Integer.parseInt(it.split(" from")[0].replace("move ", ""))
            val from = Integer.parseInt(it.split("from ")[1].split(" to ")[0]) - 1
            val to = Integer.parseInt(it.split("from ")[1].split(" to ")[1]) - 1
            
            moves.add(Move(amount, from, to))
        }
    
    return moves
}

fun generateCrateStacks(lines: List<String>): List<Stack<Char>> {
    val crateStacks = mutableListOf<Stack<Char>>()
    
    lines.forEach { 
        crateStacks.add(Stack())
    }
    
    for (i in lines.indices) { 
        for (j in 1 until lines[i].length) {
            crateStacks[i].add(lines[i][j])
        }
    }
    
    return crateStacks
}

fun getCrateMap(lines: List<String>): List<String> {
    val crateMap = mutableListOf<String>()

    run breaking@{
        lines.forEach {
            if (it.isNotEmpty()) {
                crateMap.add(it)
            } else {
                return@breaking
            }
        }
    }

    return rotateCrateMap(crateMap)
}

fun rotateCrateMap(lines: List<String>): List<String> {
    val lineLength = lines[0].length
    val rotatedMap = mutableListOf<String>()

    for (i in 0 until lineLength) {
        rotatedMap.add("")
    }
    
    for (j in lines.indices) {
        for (i in 0 until lineLength) {
            rotatedMap[i] += lines[j][i].toString()
        }
    }

    return rotatedMap
        .map { it.reversed() }
        .filterNot { it.startsWith(" ")}
        .filterNot { it.startsWith("[")}
        .map { it.trim() }
}

data class Move (val amount: Int, val from: Int, val to: Int)