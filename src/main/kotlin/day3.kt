fun day3(lines: List<String>) {
    part1(lines)
    part2(lines)
}

fun part2(lines: List<String>) {
    var prioritySum = 0

    for (i in lines.indices step 3) {
        val common = lines[i].toSet().intersect(lines[i+1].toSet()).intersect(lines[i+2].toSet())

        prioritySum += (findPriority(common.first()))
    }

    println("Day 3 part 2: $prioritySum")
}

fun part1(lines: List<String>) {
    var prioritySum = 0

    lines.forEach {
        val chunks = it.chunked(it.length/2)
        val common = chunks[0].toSet().intersect(chunks[1].toSet())

        prioritySum += (findPriority(common.first()))
    }

    println("Day 3 part 1: $prioritySum")
}

fun findPriority(letter: Char): Int {
    return if (letter.isUpperCase()) {
        letter.code - 'A'.code + 27
    } else {
        letter.code - 'a'.code + 1
    }
}