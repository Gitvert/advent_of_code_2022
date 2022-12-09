val movementMap = mapOf<Pos, Pos>(
    Pos(-2,0) to Pos(-1,0),
    Pos(0,-2) to Pos(0,-1),
    Pos(2,0) to Pos(1,0),
    Pos(0,2) to Pos(0,1),

    Pos(-2,-1) to Pos(-1, -1),
    Pos(-1,-2) to Pos(-1, -1),
    Pos(1,-2) to Pos(1, -1),
    Pos(2,-1) to Pos(1, -1),
    
    Pos(1,2) to Pos(1, 1),
    Pos(2,1) to Pos(1, 1),
    Pos(-1,2) to Pos(-1, 1),
    Pos(-2,1) to Pos(-1, 1),
    
    Pos(-2,-2) to Pos(-1, -1),
    Pos(-2,2) to Pos(-1, 1),
    Pos(2,-2) to Pos(1, -1),
    Pos(2,2) to Pos(1, 1),
)

fun day9(lines: List<String>) {
    val part1Rope = mutableListOf(Pos(0,0), Pos(0,0))
    val part2Rope = mutableListOf(
        Pos(0,0),
        Pos(0,0),
        Pos(0,0),
        Pos(0,0),
        Pos(0,0),
        Pos(0,0),
        Pos(0,0),
        Pos(0,0),
        Pos(0,0),
        Pos(0,0),
    )
    
    println("Day 9 part 1: ${solve(lines, part1Rope)}")
    println("Day 9 part 2: ${solve(lines, part2Rope)}")
}

fun solve(lines: List<String>, rope: List<Pos>): Int {
    val visited = mutableSetOf<Pos>()
    visited.add(Pos(0,0))
    
    lines.forEach { 
        val direction = it.split(" ")[0]
        val steps = Integer.parseInt(it.split(" ")[1])
        
        for (i in 0 until steps) {
            when(direction) {
                "U" -> rope[0].y--
                "D" -> rope[0].y++
                "L" -> rope[0].x--
                "R" -> rope[0].x++
            }
            
            for (j in 1 until rope.size) {
                val diff = Pos(rope[j-1].x - rope[j].x, rope[j-1].y - rope[j].y)
                val moveDirection = movementMap.getOrDefault(diff, Pos(0,0))
                
                rope[j].x += moveDirection.x
                rope[j].y += moveDirection.y
                
                if (j == rope.size - 1) {
                    visited.add(Pos(rope[j].x, rope[j].y))
                }
            }
        }
    }
    
    return visited.size
}

data class Pos(var x: Int, var y: Int)