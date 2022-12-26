fun day19(lines: List<String>) {
    var answer = 0

    /*lines.forEachIndexed { index, it -> 
         println("Starting iteration $index, answer is now $answer")
         answer += part1(it)
     }*/

    println("Day 19 part 1: $answer")
}

fun part1(line: String): Int {
    val robotRecipes = mutableListOf<RobotRecipe>()
    
    val regex = Regex("\\d+")
    val results = regex.findAll(line).map { it.value.toInt() }.toList()
    robotRecipes.add(RobotRecipe(Robot(1, 0, 0, 0), results[1], 0, 0))
    robotRecipes.add(RobotRecipe(Robot(0, 1, 0, 0), results[2], 0, 0))
    robotRecipes.add(RobotRecipe(Robot(0, 0, 1, 0 ), results[3], results[4], 0))
    robotRecipes.add(RobotRecipe(Robot(0, 0, 0, 1), results[5], 0, results[6]))
    
    if (robotRecipes[3].obsidianCost > 19) {
        return 0
    }

    var possibleStates = mutableSetOf(State(Resources(0, 0, 0, 0), Robot(1, 0, 0, 0)))

    for (i in 1..24) {
        increaseResources(possibleStates)

        spawnNewPossibleStates(possibleStates, robotRecipes, i)

        possibleStates = removeNonsenseStates(possibleStates.toMutableList())
        possibleStates = removeNonsenseStates2(possibleStates.toMutableList())
        
        println("Completed minute $i")
    }

    val maxGeodeCount = possibleStates.maxOf {
        it.resources.geode
    }
    
    return results[0] * maxGeodeCount
}

fun removeNonsenseStates2(possibleStates: MutableList<State>): MutableSet<State> {
    val indexesToRemove = mutableListOf<Int>()
    val maxGeodeSpeed = possibleStates.maxOf { it.robots.geodeSpeed }

    possibleStates.forEachIndexed { index, it ->
        if ((it.resources.ore > 25 && it.robots.obsidianSpeed < 1) || (it.resources.clay > 25 && it.robots.obsidianSpeed < 1) || (it.resources.obsidian > 25 && it.robots.geodeSpeed < 1) || it.robots.geodeSpeed + 1 < maxGeodeSpeed) {
            indexesToRemove.add(index)
        }
    }

    indexesToRemove.sortDescending()
    indexesToRemove.forEach { 
        possibleStates.removeAt(it)
    }

    return possibleStates.toMutableSet()
}

fun removeNonsenseStates(possibleStates: MutableList<State>): MutableSet<State> {
    val indexesToRemove = mutableSetOf<Int>()
    
    possibleStates.forEachIndexed { index, state -> 
        possibleStates.forEachIndexed { innerIndex, innerState -> 
            if (index != innerIndex) {
                if (state.robots.oreSpeed == innerState.robots.oreSpeed && state.robots.claySpeed == innerState.robots.claySpeed && state.robots.obsidianSpeed == innerState.robots.obsidianSpeed && state.robots.geodeSpeed == innerState.robots.geodeSpeed) {
                    if (state.resources.ore == innerState.resources.ore && state.resources.clay == innerState.resources.clay && state.resources.obsidian == innerState.resources.obsidian && state.resources.geode < innerState.resources.geode) {
                        indexesToRemove.add(index)
                    } else if (state.resources.ore == innerState.resources.ore && state.resources.clay == innerState.resources.clay && state.resources.obsidian < innerState.resources.obsidian && state.resources.geode == innerState.resources.geode) {
                        indexesToRemove.add(index)
                    } else if (state.resources.ore == innerState.resources.ore && state.resources.clay < innerState.resources.clay && state.resources.obsidian == innerState.resources.obsidian && state.resources.geode == innerState.resources.geode) {
                        indexesToRemove.add(index)
                    } else if (state.resources.ore < innerState.resources.ore && state.resources.clay == innerState.resources.clay && state.resources.obsidian == innerState.resources.obsidian && state.resources.geode == innerState.resources.geode) {
                        indexesToRemove.add(index)
                    }
                } else {
                    if (state.robots.oreSpeed == innerState.robots.oreSpeed && state.robots.claySpeed == innerState.robots.claySpeed && state.robots.obsidianSpeed == innerState.robots.obsidianSpeed && state.robots.geodeSpeed < innerState.robots.geodeSpeed) {
                        indexesToRemove.add(index)
                    }
                }
            }
        }
    }
    
    val toRemove = indexesToRemove.toMutableList()

    toRemove.sortDescending()
    toRemove.forEach { 
        possibleStates.removeAt(it)
    }

    return possibleStates.toMutableSet()
}

fun spawnNewPossibleStates(possibleStates: MutableSet<State>, robotRecipes: List<RobotRecipe>, iteration: Int) {
    val newStates = mutableSetOf<State>()
    
    possibleStates.forEach { 
        if (robotRecipes[0].oreCost <= it.resources.ore) {
            val newState = State(
                Resources(
                    it.resources.ore - robotRecipes[0].oreCost - 1, 
                    it.resources.clay, 
                    it.resources.obsidian,
                    it.resources.geode
                ),
                Robot(
                    it.robots.oreSpeed + 1, 
                    it.robots.claySpeed,
                    it.robots.obsidianSpeed, 
                    it.robots.geodeSpeed
                )
            )
            newStates.add(newState)
        }
        
        if (robotRecipes[1].oreCost <= it.resources.ore) {
            val newState = State(
                Resources(
                    it.resources.ore - robotRecipes[1].oreCost,
                    it.resources.clay - 1,
                    it.resources.obsidian,
                    it.resources.geode
                ),
                Robot(
                    it.robots.oreSpeed,
                    it.robots.claySpeed + 1,
                    it.robots.obsidianSpeed,
                    it.robots.geodeSpeed
                )
            )
            newStates.add(newState)
        }
        
        if (robotRecipes[2].oreCost <= it.resources.ore && robotRecipes[2].clayCost <= it.resources.clay) {
            val newState = State(
                Resources(
                    it.resources.ore - robotRecipes[2].oreCost,
                    it.resources.clay - robotRecipes[2].clayCost,
                    it.resources.obsidian - 1,
                    it.resources.geode
                ),
                Robot(
                    it.robots.oreSpeed,
                    it.robots.claySpeed,
                    it.robots.obsidianSpeed + 1,
                    it.robots.geodeSpeed
                )
            )
            newStates.add(newState)
        }
        
        if (robotRecipes[3].oreCost <= it.resources.ore && robotRecipes[3].obsidianCost <= it.resources.obsidian) {
            it.resources.ore -= robotRecipes[3].oreCost
            it.resources.obsidian -= robotRecipes[3].obsidianCost
            if (iteration != 24) { it.resources.geode-- } else { it.resources.geode }
            it.robots.geodeSpeed++
        }
    }
    
    possibleStates.addAll(newStates)
}

fun increaseResources(possibleStates: Set<State>) {
    possibleStates.forEach { 
        it.resources.ore += it.robots.oreSpeed
        it.resources.clay += it.robots.claySpeed
        it.resources.obsidian += it.robots.obsidianSpeed
        it.resources.geode += it.robots.geodeSpeed
    }
}

fun getTotalResources(resources: Resources): Int {
    return resources.ore + resources.clay + resources.obsidian + resources.geode
}

fun getTotalMiningSpeed(robot: Robot): Int {
    return robot.claySpeed + robot.oreSpeed + robot.obsidianSpeed + robot.geodeSpeed
}

data class RobotRecipe(val produces: Robot, val oreCost: Int, val clayCost: Int, val obsidianCost: Int)

data class Robot(var oreSpeed: Int, var claySpeed: Int, var obsidianSpeed: Int, var geodeSpeed: Int)

data class Resources(var ore: Int, var clay: Int, var obsidian: Int, var geode: Int)

data class State(val resources: Resources, var robots: Robot)