fun day4(lines: List<String>) {
    var fullyContainedCount = 0
    var partiallyContainedCount = 0

    lines.forEach {
        val first = it.split(",")[0]
        val second = it.split(",")[1]

        val firstRange = Integer.parseInt(first.split("-")[0])..Integer.parseInt(first.split("-")[1])
        val secondRange = Integer.parseInt(second.split("-")[0])..Integer.parseInt(second.split("-")[1])

        val intersect = firstRange.intersect(secondRange)

        if (intersect.isNotEmpty()) {
            partiallyContainedCount++

            if (intersect.size == firstRange.count() || intersect.size == secondRange.count()) {
                fullyContainedCount++
            }
        }
    }

    println("Day 4 part 1: $fullyContainedCount")
    println("Day 4 part 2: $partiallyContainedCount")
}