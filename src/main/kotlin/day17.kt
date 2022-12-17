val BAR = listOf(Pos(0,0), Pos(1,0), Pos(2,0), Pos(3,0))

val PLUS = listOf(Pos(0,-1), Pos(1,-2), Pos(1,-1), Pos(1,0), Pos(2,-1))

val REVERSE_L = listOf(Pos(0,0), Pos(1,0), Pos(2,0), Pos(2,-1), Pos(2,-2))

val I = listOf(Pos(0,-3), Pos(0,-2), Pos(0,-1), Pos(0,0))

val CUBE = listOf(Pos(0,-1), Pos(1,-1), Pos(0,0), Pos(1,0))

fun day17(lines: List<String>) {
    val jetStream = lines[0].toCharArray().toMutableList()
    val rocks = createRocks()
    val chamber = Array( 400000 ) { CharArray(7) { '.' } }

    simulateFallingRocks(jetStream, rocks, chamber)
}

fun printChamber(chamber: Array<CharArray>) {
    chamber.forEach { 
        if (it.joinToString("") != ".......") {
            println(it)
        }
    }
}

fun simulateFallingRocks(jetStream: MutableList<Char>, rocks: MutableList<List<Pos>>, chamber: Array<CharArray>) {
    val jetStreamCopy = jetStream.toList()
    val rocksCopy = rocks.toList()
    var bottom = chamber.size - 1
    
    var heightBeforePatternStart = 0L
    var heightAfterFirstPattern: Long
    var heightOfRepeatingPattern: Long
    var heightAtTheEnd = 0L
    
    for (i in 1..114920) {
        val rock = spawnRock(bottom, rocks)
        
        while (true) {
            if (jetStream.isEmpty()) {
                jetStream.addAll(jetStreamCopy)
            }
            if (rocks.isEmpty()) {
                rocks.addAll(rocksCopy)
            }
            jetPush(rock, jetStream.removeFirst(), chamber)
            moveDown(rock)
            if (hitBottom(rock, chamber)) {
                moveUp(rock)
                updateChamber(rock, chamber)
                val maybeNewBottom = rock.minOf { it.y } - 1
                if (maybeNewBottom < bottom) {
                    bottom = maybeNewBottom
                }
                break
            }
        }
        when (i) {
            2022 -> {
                println("Day 17 part 1: ${chamber.size - 1L - bottom}")
            }
            113205 -> {
                heightBeforePatternStart = chamber.size - 1L - bottom
            }
            114920 -> {
                heightAfterFirstPattern = chamber.size - 1L - bottom
                heightOfRepeatingPattern = heightAfterFirstPattern - heightBeforePatternStart
                heightAtTheEnd = heightOfRepeatingPattern * 583090312 + heightAfterFirstPattern
                println("Day 17 part 2: $heightAtTheEnd")
            }
        }
    }
}

fun updateChamber(rock: List<Pos>, chamber: Array<CharArray>) {
    rock.forEach { 
        chamber[it.y][it.x] = '#'
    }
}

fun hitBottom(rock: List<Pos>, chamber: Array<CharArray>): Boolean {
    var hitBottom = false
    
    rock.forEach { 
        if (it.y >= chamber.size || chamber[it.y][it.x] == '#') {
            hitBottom = true
        }
    }
    
    return hitBottom
}

fun jetPush(rock: List<Pos>, direction: Char, chamber: Array<CharArray>) {
    val min = rock.minOf { it.x }
    val max = rock.maxOf { it.x }
    
    if (direction == '<' && min == 0) {
        return
    }
    
    if (direction == '>' && max == 6) {
        return
    }
    
    val change = if (direction == '<') { -1 } else { 1}
    var hitRock = false
    
    rock.forEach {
        if (chamber[it.y][it.x + change] == '#') {
            hitRock = true
        }
    }
    
    if (!hitRock) {
        rock.forEach {
            it.x += change
        }
    }
}

fun moveUp(rock: List<Pos>) {
    rock.forEach {
        it.y--
    }
}

fun moveDown(rock: List<Pos>) {
    rock.forEach { 
        it.y++
    }
}

fun spawnRock(bottom: Int, rocks: MutableList<List<Pos>>): List<Pos> {
    val rock = rocks.removeFirst()
    
    return rock.map { Pos(it.x + 2, it.y + bottom - 3) }
}

fun createRocks(): MutableList<List<Pos>> {
    val rocks = mutableListOf<List<Pos>>()
    
    rocks.add(BAR)
    rocks.add(PLUS)
    rocks.add(REVERSE_L)
    rocks.add(I)
    rocks.add(CUBE)
    
    return rocks
}
