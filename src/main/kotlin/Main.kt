import java.io.File

fun main(args: Array<String>) {

    day1(readFile("day01.txt"))
    day2(readFile("day02.txt"))
}

fun readFile(fileName: String): List<String> {
    val lines: MutableList<String> = mutableListOf()

    File("src/main/kotlin/inputs/$fileName").useLines { line -> line.forEach { lines.add(it) } }

    return lines
}