fun day7(lines: List<String>) {
    val startDir = buildFileTree(lines.subList(1, lines.size))
    val dirSizes: MutableList<Pair<String, Long>> = mutableListOf()
    findDirSize(startDir, dirSizes)
    
    getPart1Answer(dirSizes)
    getPart2Answer(dirSizes)
}

fun getPart1Answer(dirSizes: MutableList<Pair<String, Long>>) {
    val sizeSum = dirSizes
        .filter { it.second <= 100000L }
        .sumOf { it.second }
    
    println("Day 7 part 1: $sizeSum")
}

fun getPart2Answer(dirSizes: MutableList<Pair<String, Long>>) {
    val usedSpace = dirSizes.find { it.first == "/" }!!.second
    val unusedSpace =  70000000L - usedSpace
    val neededSpace = 30000000L - unusedSpace
    
    val dirToDelete = dirSizes
        .filter { it.second >= neededSpace }
        .minByOrNull { it.second }!!

    println("Day 7 part 2: ${dirToDelete.second}")
}

fun findDirSize(dir: Dir, dirSizes: MutableList<Pair<String, Long>>): Long {
    var dirSize = dir.files.sumOf { it.size }

    if (dir.childDirs.isNotEmpty()) {
        var childDirSize = 0L
        dir.childDirs.forEach {
            childDirSize += findDirSize(it, dirSizes)
        }

        dirSize += childDirSize
    }
    
    dirSizes.add(Pair(dir.name, dirSize))
    return dirSize
}

fun buildFileTree(lines: List<String>): Dir {
    val startDir = Dir(null, mutableListOf(), mutableListOf(), "/")
    var currentDir = startDir
    
    lines.forEach {instruction -> 
        if (instruction.startsWith("$")) {
            if (instruction.substring(2, 4) == "cd") {
                currentDir = if (instruction.substring(5) == "..") {
                    currentDir.parent!!
                } else {
                    currentDir.childDirs.find { it.name == instruction.substring(5) }!!
                }
            }
        } else if (instruction.startsWith("dir")) {
            val newDir = Dir(currentDir, mutableListOf(), mutableListOf(), instruction.substring(4))
            currentDir.childDirs.add(newDir)
        } else {
            val newFile = File(instruction.split(" ")[0].toLong(), instruction.split(" ")[1])
            currentDir.files.add(newFile)
        }
    }
    
    return startDir
}

data class Dir (val parent: Dir?, val childDirs: MutableList<Dir>, val files: MutableList<File>, val name: String)
data class File (val size: Long, val name: String)