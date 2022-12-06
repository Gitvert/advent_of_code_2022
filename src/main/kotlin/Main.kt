import java.io.File

val daySolvers = listOf(::day1, ::day2, ::day3, ::day4, ::day5, ::day6)

fun main(args: Array<String>) {

    for (i in 1..daySolvers.size) {
        daySolvers[i-1](readFile("day$i.txt"))
        println()
    }
}

fun readFile(fileName: String): List<String> {
    val lines: MutableList<String> = mutableListOf()

    File("src/main/kotlin/inputs/$fileName").useLines { line -> line.forEach { lines.add(it) } }

    return lines
}