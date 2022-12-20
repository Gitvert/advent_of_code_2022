import kotlin.math.abs

fun day20(lines: List<String>) {
    val part1numbers = lines.mapIndexed { index, line -> 
        ListNumber(line.toLong(), index)
    }.toMutableList()
    
    for (i in part1numbers.indices) {
        moveNumber(part1numbers, i)
    }
    
    println("Day 20 part 1: ${findGroveCoordinates(part1numbers)}")
    
    val part2Numbers = lines.mapIndexed { index, line ->
        ListNumber(line.toLong() * 811589153L, index)
    }.toMutableList()

    for (i in 0 until 10) {
        for (j in part2Numbers.indices) {
            moveNumber(part2Numbers, j)
        }
    }

    println("Day 20 part 2: ${findGroveCoordinates(part2Numbers)}")
}

fun findGroveCoordinates(numbers: MutableList<ListNumber>): Long {    
    val n1000 = numbers[getCircularIndex(1000, numbers)].number
    val n2000 = numbers[getCircularIndex(2000, numbers)].number
    val n3000 = numbers[getCircularIndex(3000, numbers)].number
    
    return n1000 + n2000 + n3000
}

fun getCircularIndex(index: Int, numbers: MutableList<ListNumber>): Int {
    val lastIndex = numbers.indices.last
    val zeroIndex = numbers.indexOfFirst { it.number == 0L }
    
    var i = index
    var pos = zeroIndex

    while (i > 0) {
        pos++
        if (pos > lastIndex) {
            pos = 0
        }
        i--
    }
    
    return pos
}

fun moveNumber(numbers: MutableList<ListNumber>, startIndex: Number) {
    val lastIndex = numbers.indices.last
    val position = numbers.indexOfFirst { it.originalIndex == startIndex }
    val number = numbers[position]
    
    if (number.number == 0L) {
        return
    }
    
    val steps = getSteps(lastIndex, number.number)
    
    var newPosition = position + steps
    
    if (newPosition > lastIndex) {
        newPosition %= lastIndex
    }
    
    numbers.removeAt(position)
    numbers.add(newPosition.toInt(), number)
}

fun getSteps(lastIndex: Int, number: Long): Long {
    val steps = if (number < 0) {
        lastIndex - (abs(number) % lastIndex)
    } else {
        number
    }

    return steps
}

data class ListNumber(val number: Long, val originalIndex: Int)