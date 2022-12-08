import kotlin.math.abs

fun day8(lines: List<String>) {
    val gridSize = lines.size
    val visibleMap = Array(gridSize) { IntArray(gridSize) }
    val treeMap = getTreeMap(lines)
    
    markRows(treeMap, visibleMap)
    markColumns(treeMap, visibleMap)
    
    println("Day 8 part 1: ${visibleMap.sumOf { rows -> rows.sumOf { it } }}")
    println("Day 8 part 2: ${findHighestScenicScore(treeMap)}")
}

fun findHighestScenicScore(treeMap: Array<IntArray>): Int {
    var highestScenicScore = -1
    
    treeMap.forEachIndexed { x, row ->
        row.forEachIndexed { y, _ ->
            val currentScenicScore = calculateScenicScore(x, y, treeMap)
            if (currentScenicScore > highestScenicScore) {
                highestScenicScore = currentScenicScore
            }
        }
    }
    
    return highestScenicScore
}

fun calculateScenicScore(x: Int, y: Int, treeMap: Array<IntArray>): Int {
    val gridSize = treeMap.size
    var scenicScore = 1
    
    //Check right
    for (i in y until gridSize) {
        if ((y != i && treeMap[x][y] <= treeMap[x][i]) || i == gridSize - 1) {
            scenicScore *= abs(y - i)
            break
        }
    }
    
    //Check left
    for (i in y downTo 0) {
        if ((y != i && treeMap[x][y] <= treeMap[x][i]) || i == 0) {
            scenicScore *= abs(y - i)
            break
        }
    }
    
    //Check down
    for (i in x until gridSize) {
        if ((x != i && treeMap[x][y] <= treeMap[i][y] ) || i == gridSize - 1) {
            scenicScore *= abs(x - i)
            break
        }
    }
    
    //check up
    for (i in x downTo 0) {
        if ((x != i && treeMap[x][y] <= treeMap[i][y]) || i == 0) {
            scenicScore *= abs(x - i)
            break
        }
    }
    
    return scenicScore
}

fun markRows(treeMap: Array<IntArray>, visibleMap: Array<IntArray>) {
    treeMap.forEachIndexed { i, it -> 
        var tallestInRowLeftToRight = -1
        var tallestInRowRightToLeft = -1
        for (j in it.indices) {
            if (it[j] > tallestInRowLeftToRight) {
                tallestInRowLeftToRight = it[j]
                visibleMap[i][j] = 1
            }
        }
        
        for (j in it.indices.reversed()) {
            if (it[j] > tallestInRowRightToLeft) {
                tallestInRowRightToLeft = it[j]
                visibleMap[i][j] = 1
            }
        }
    }
}

fun markColumns(treeMap: Array<IntArray>, visibleMap: Array<IntArray>) {
    for (i in treeMap.indices) {
        var tallestInColumnTopToBottom = -1
        var tallestInColumnBottomToTop = -1
        for (j in treeMap.indices) {
            if (treeMap[j][i] > tallestInColumnTopToBottom) {
                tallestInColumnTopToBottom = treeMap[j][i]
                visibleMap[j][i] = 1
            }
        }

        for (j in treeMap.indices.reversed()) {
            if (treeMap[j][i] > tallestInColumnBottomToTop) {
                tallestInColumnBottomToTop = treeMap[j][i]
                visibleMap[j][i] = 1
            }
        }
    }
}

fun getTreeMap(lines: List<String>): Array<IntArray> {
    val gridSize = lines.size
    val treeMap = Array(gridSize) { IntArray(gridSize) }

    lines.forEachIndexed { i, row ->
        row.forEachIndexed { j, _ ->
            treeMap[i][j] = Integer.valueOf(row[j].toString())
        }
    }

    return treeMap
}
