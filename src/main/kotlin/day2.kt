fun day2(lines: List<String>) {
    val part1Outcomes = mapOf(
        "A X" to 4,
        "A Y" to 8,
        "A Z" to 3,
        "B X" to 1,
        "B Y" to 5,
        "B Z" to 9,
        "C X" to 7,
        "C Y" to 2,
        "C Z" to 6
    )

    val part2Outcomes = mapOf(
        "A X" to 3,
        "A Y" to 4,
        "A Z" to 8,
        "B X" to 1,
        "B Y" to 5,
        "B Z" to 9,
        "C X" to 2,
        "C Y" to 6,
        "C Z" to 7
    )

    var totalScorePart1 = 0
    var totalScorePart2 = 0

    lines.forEach {
        totalScorePart1 += part1Outcomes[it]!!
        totalScorePart2 += part2Outcomes[it]!!
    }

    println("Day 2 part 1: $totalScorePart1")
    println("Day 2 part 2: $totalScorePart2")
}