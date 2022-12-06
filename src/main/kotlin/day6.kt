fun day6(lines: List<String>) {
    val message = lines[0]

    println("Day 6 part 1: ${findMarker(message, 4)}")
    println("Day 6 part 2: ${findMarker(message, 14)}")
}

fun findMarker(message: String, markerSize: Int): Int {
    for (i in 0.. message.length - markerSize) {
        val potentialStartOfPacketMarker = message.substring(i, i + markerSize)

        if (potentialStartOfPacketMarker.toSet().size == markerSize) {
            return i + markerSize
        }
    }
    
    return -1
}