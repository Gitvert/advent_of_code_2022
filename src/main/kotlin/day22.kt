import kotlin.math.abs
 
val FIRST_RANGE = 0..49
val SECOND_RANGE = 50..99
val THIRD_RANGE = 100..149

fun day22(lines: List<String>) {
    val world = parseMap(lines)
    val instructions = parseInstructions(lines.last())
    
    val playerPart1 = findStartPosition(world)
    followPart1Instructions(world, instructions, playerPart1)
    
    println("Day 22 part 1: ${calculatePassword(playerPart1)}")

    val playerPart2 = findStartPosition(world)
    followPart2Instructions(world, instructions, playerPart2)
    
    println("Day 22 part 2: ${calculatePassword(playerPart2)}")
}

fun followPart2Instructions(world: Array<CharArray>, instructions: List<PathInstruction>, player: Player) {
    instructions.forEach {
        val steps = it.steps
        val direction = it.direction

        walkPart2(steps, player, world)

        when (player.direction) {
            'L' -> { player.direction = if (direction == 'R') { 'U' } else if (direction == 'L') { 'D' } else { 'L' } }
            'R' -> { player.direction = if (direction == 'R') { 'D' } else if (direction == 'L') { 'U' } else { 'R' } }
            'U' -> { player.direction = if (direction == 'R') { 'R' } else if (direction == 'L') { 'L' } else { 'U' } }
            'D' -> { player.direction = if (direction == 'R') { 'L' } else if (direction == 'L') { 'R' } else { 'D' } }
        }
    }
}

fun walkPart2(steps: Int, player: Player, world: Array<CharArray>) {
    for (i in 0 until steps) {
        val lastKnownPosition = Player(player.x, player.y, player.direction)
        val newDirection = findNextPosition(player)
        
        if (world[player.y][player.x] == '#') {
            player.x = lastKnownPosition.x
            player.y = lastKnownPosition.y
            return
        } else {
            if (player.direction != newDirection) {
                player.direction = newDirection
            }
        }
    }
}

fun findNextPosition(player: Player): Char {
    if (player.x in SECOND_RANGE && player.y in FIRST_RANGE) {
        return moveInFirstSide(player)
    } else if (player.x in THIRD_RANGE && player.y in FIRST_RANGE) {
        return moveInSecondSide(player)
    } else if (player.x in SECOND_RANGE && player.y in SECOND_RANGE) {
        return moveInThirdSide(player)
    } else if (player.x in SECOND_RANGE && player.y in THIRD_RANGE) {
        return moveInFourthSide(player)
    } else if (player.x in FIRST_RANGE && player.y in THIRD_RANGE) {
        return moveInFifthSide(player)
    } else {
        return moveInSixthSide(player)
    }
}

fun moveInFirstSide(player: Player): Char {
    when (player.direction) {
        'L' -> {
            return if (player.x == 50) {
                player.x = 0
                player.y = 149 - player.y
                'R'
            } else {
                player.x--
                player.direction
            }
        }
        'R' -> {
            player.x++
            return player.direction
        }
        'U' -> {
            return if (player.y == 0) {
                player.y = player.x + 100
                player.x = 0
                'R'
            } else {
                player.y--
                player.direction
            }
        }
        else -> {
            player.y++
            return player.direction
        }
    }
}

fun moveInSecondSide(player: Player): Char {
    when (player.direction) {
        'L' -> {
            player.x--
            return player.direction
        }
        'R' -> {
            return if (player.x == 149) {
                player.x = 99
                player.y = 149 - player.y
                'L'
            } else {
                player.x++
                player.direction
            }
        }
        'U' -> {
            if (player.y == 0) {
                player.x -= 100
                player.y = 199
            } else {
                player.y--
            }
            return player.direction
        }
        else -> {
            return if (player.y == 49) {
                player.y = player.x - 50
                player.x = 99
                'L'
            } else {
                player.y++
                player.direction
            }
        }
    }
}

fun moveInThirdSide(player: Player): Char {
    when (player.direction) {
        'L' -> {
            return if (player.x == 50) {
                player.x = player.y - 50
                player.y = 100
                'D'
            } else {
                player.x--
                player.direction
            }
        }
        'R' -> {
            return if (player.x == 99) {
                player.x = player.y + 50
                player.y = 49
                'U'
            } else {
                player.x++
                player.direction
            }
        }
        'U' -> {
            player.y--
            return player.direction
        }
        else -> {
            player.y++
            return player.direction
        }
    }
}

fun moveInFourthSide(player: Player): Char {
    when (player.direction) {
        'L' -> {
            player.x--
            return player.direction
        }
        'R' -> {
            return if (player.x == 99) {
                player.x = 149
                player.y = 149 - player.y
                'L'
            } else {
                player.x++
                player.direction
            }
        }
        'U' -> {
            player.y--
            return player.direction
        }
        else -> {
            return if (player.y == 149) {
                player.y = player.x + 100
                player.x = 49
                'L'
            } else {
                player.y++
                return player.direction
            }
        }
    }
}

fun moveInFifthSide(player: Player): Char {
    when (player.direction) {
        'L' -> {
            return if (player.x == 0) {
                player.x = 50
                player.y = 149 - player.y
                'R'
            } else {
                player.x--
                player.direction
            }
        }
        'R' -> {
            player.x++
            return player.direction
        }
        'U' -> {
            return if (player.y == 100) {
                player.y = player.x + 50
                player.x = 50
                'R'
            } else {
                player.y--
                player.direction
            }
        }
        else -> {
            player.y++
            return player.direction
        }
    }
}

fun moveInSixthSide(player: Player): Char {
    when (player.direction) {
        'L' -> {
            return if (player.x == 0) {
                player.x = player.y - 100
                player.y = 0
                'D'
            } else {
                player.x--
                player.direction
            }
        }
        'R' -> {
            return if (player.x == 49) {
                player.x = player.y - 100
                player.y = 149
                'U'
            } else {
                player.x++
                player.direction
            }
        }
        'U' -> {
            player.y--
            return player.direction
        }
        else -> {
            return if (player.y == 199) {
                player.x += 100
                player.y = 0
                player.direction
            } else {
                player.y++
                player.direction
            }
        }
    }
}

fun followPart1Instructions(world: Array<CharArray>, instructions: List<PathInstruction>, player: Player) {
    instructions.forEach { 
        val steps = it.steps
        val direction = it.direction
        
        when(player.direction) {
            'L' -> {
                walkPart1(steps * -1, 0, player, world)
                player.direction = if (direction == 'R') { 'U' } else if (direction == 'L') { 'D' } else { 'L' }
            }
            'R' -> {
                walkPart1(steps, 0, player, world)
                player.direction = if (direction == 'R') { 'D' } else if (direction == 'L') { 'U' } else { 'R' }
            }
            'U' -> {
                walkPart1(0, steps * -1, player, world)
                player.direction = if (direction == 'R') { 'R' } else if (direction == 'L') { 'L' } else { 'U' }
            }
            'D' -> {
                walkPart1(0, steps, player, world)
                player.direction = if (direction == 'R') { 'L' } else if (direction == 'L') { 'R' } else { 'D' }
            }
        }
    }
}

fun calculatePassword(player: Player): Int {
    return (player.y + 1) * 1000 + (player.x + 1) * 4 + getDirectionValue(player.direction)
}

fun getDirectionValue(direction: Char): Int {
    return when(direction) {
        'R' -> 0
        'D' -> 1
        'L' -> 2
        else -> 3
    }
}

fun walkPart1(x: Int, y: Int, player: Player, world: Array<CharArray>) {
    var lastValidPosition = Player(player.x, player.y, player.direction)

    var i = 0
    
    while (i < abs(x)) {
        val xPos = if (x > 0) { 
            (player.x + 1) % world[0].size 
        } else { 
            val temp = player.x - 1
            if (temp < 0) {
                world[0].size - 1
            } else {
                temp
            }
        }

        if(world[player.y][xPos] == '#') {
            player.x = lastValidPosition.x
            player.y = lastValidPosition.y
            
            return
        }

        player.x = xPos

        if (world[player.y][player.x] == ' ') {
            i--
        }
        else {
            lastValidPosition = Player(player.x, player.y, player.direction)
        }
        i++
    }
    
    i = 0

    while (i < abs(y)) {
        val yPos = if (y > 0) {
            (player.y + 1) % world.size
        } else {
            val temp = player.y -1
            if (temp < 0) {
                world.size - 1
            } else {
                temp
            }
        }

        if(world[yPos][player.x] == '#') {
            player.x = lastValidPosition.x
            player.y = lastValidPosition.y
            
            return
        }

        player.y = yPos

        if(world[player.y][player.x] == ' ') {
            i--
        }
        else {
            lastValidPosition = Player(player.x, player.y, player.direction)
        }
        i++
    }
}

fun findStartPosition(world: Array<CharArray>): Player {
    world[0].forEachIndexed { index, it ->  
        if (it == '.') {
            return Player(index, 0, 'R')
        }
    }
    
    return Player(0, 0, 'R')
}

fun parseInstructions(line: String): List<PathInstruction> {
    val instructions = mutableListOf<PathInstruction>()
    var instruction = ""
    line.forEach { 
        if (it == 'L' || it == 'R') {
            instructions.add(PathInstruction(instruction.toInt(), it))
            instruction = ""
        } else {
            instruction += it
        }
    }

    instructions.add(PathInstruction(instruction.toInt(), '-'))
    
    return instructions
}

fun parseMap(lines: List<String>): Array<CharArray> {
    var maxLength = -1
    var noOfLines = -2
    
    lines.forEach { 
        noOfLines++
        
        if (it.length > maxLength && !it[0].isDigit()) {
            maxLength = it.length
        }
    }

    val world = Array(noOfLines) { CharArray(maxLength) { ' ' } }

    for (i in 0 until noOfLines) {
        for (j in 0 until maxLength) {
            if (j < lines[i].length) {
                world[i][j] = lines[i][j]
            }
        }
    }
    
    return world
}

data class PathInstruction(val steps: Int, val direction: Char)

data class Player(var x: Int, var y: Int, var direction: Char)