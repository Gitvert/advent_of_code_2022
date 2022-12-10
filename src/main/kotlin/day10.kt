fun day10(lines: List<String>) {
    val cyclesToCheck = listOf(20, 60, 100, 140, 180, 220)
    var index = 0
    var currentInstruction = Instruction(0, 0)
    var xRegister = 1
    var signalStrength = 0
    var pixelPosition = 0
    val crtScreen = mutableListOf<Char>()
    
    for (i in 1 .. 240) {
        if (currentInstruction.cyclesLeft == 0) {
            xRegister += currentInstruction.value
            currentInstruction = getNextInstruction(lines[index])
            index++
        }
        
        if (cyclesToCheck.contains(i)) {
            signalStrength += i * xRegister
        }
        
        if ((xRegister - 1 .. xRegister + 1).contains(pixelPosition % 40)) {
            crtScreen.add('#')
        } else {
            crtScreen.add('.')
        }
        
        currentInstruction.cyclesLeft--
        pixelPosition++
    }
    
    println("Day 10 part 1: $signalStrength")
    printScreen(crtScreen)
}

fun printScreen(crtScreen: List<Char>) {
    print("Day 10 part 2:")
    
    crtScreen.forEachIndexed { index, c ->
        if (index % 40 == 0) {
            println()
        }
        print(c)
    }
    println()
}

fun getNextInstruction(line: String): Instruction {
    return if (line == "noop") { 
        Instruction(0, 1) 
    } else {
        Instruction(Integer.parseInt(line.split(" ")[1]), 2) 
    }
}

data class Instruction(val value: Int, var cyclesLeft: Int) 