fun day19(lines: List<String>) {
    var answer = 0

    lines.forEachIndexed { index, it -> 
         println("Starting iteration $index, answer is now $answer")
         answer += part1(it)
     }

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
    
    val maxOreCost = robotRecipes.maxOf { it.oreCost }
    val maxClayCost = robotRecipes.maxOf { it.clayCost }
    val maxObsidianCost = robotRecipes.maxOf { it.obsidianCost }

    val possibleStates = mutableSetOf(State(Resources(0, 0, 0, 0), Robot(1, 0, 0, 0), mutableListOf("Ore", "Clay", "Obsidian", "Geode")))

    for (i in 1..24) {
        //println(java.util.Calendar.getInstance().time)
        //println("Starting minute $i with max obsidian speed: ${possibleStates.maxOf { it.robots.obsidianSpeed }} and geode speed: ${possibleStates.maxOf { it.robots.geodeSpeed }} and max geode gathered: ${possibleStates.maxOf { it.resources.geode }}")
        println("${java.util.Calendar.getInstance().time} starting minute $i")
        increaseResources(possibleStates)

        if (i < 24) {
            spawnNewPossibleStates(possibleStates, robotRecipes, i, maxOreCost, maxClayCost, maxObsidianCost)
            removeNonsenseStates(possibleStates, 24 - i, robotRecipes)
            //removeNonsenseStates2(possibleStates)
        }
    }

    val maxGeodeCount = possibleStates.maxOf {
        it.resources.geode
    }
    
    return results[0] * maxGeodeCount
}

fun removeNonsenseStates(possibleStates: MutableSet<State>, minutesLeft: Int, robotRecipes: List<RobotRecipe>) {
    val indexesToRemove = mutableSetOf<Int>()
    //val maxGeodeSpeed = possibleStates.maxOf { it.robots.geodeSpeed }
    val maxGeodeCount = possibleStates.maxOf { it.resources.geode }
    val miningSpeedFromMaxGeodeCount = possibleStates.maxBy { it.resources.geode }.robots.geodeSpeed
    //val oreMiningSpeedFromMaxGeodeCount = possibleStates.maxBy { it.resources.geode }.robots.oreSpeed

    possibleStates.forEachIndexed { index, it ->
        
        /*if (it.robots.geodeSpeed + 2 < maxGeodeSpeed) {
            indexesToRemove.add(index)
        }*/
        
        /*if (minutesLeft < 10 && it.robots.claySpeed == 0) {
            indexesToRemove.add(index)
        }*/
        
        if (it.resources.geode < maxGeodeCount && minutesLeft < 7) {
            val worstFromOtherState = maxGeodeCount + (miningSpeedFromMaxGeodeCount * minutesLeft)
            val bestFromThisState = it.resources.geode + getMaxGeodeProduction(it, minutesLeft)

            if (bestFromThisState < worstFromOtherState) {
                indexesToRemove.add(index)
            }
            
            /*if (it.robots.oreSpeed - 2 > oreMiningSpeedFromMaxGeodeCount) {
                indexesToRemove.add(index)
            }*/
        }
        
        if (canAffordAllRobots(it, robotRecipes)) {
            indexesToRemove.add(index)
        }
        
        if (canAffordAllAvailableRobots(it, robotRecipes)) {
            indexesToRemove.add(index)
        }

        if (canAffordAllAvailableRobots2(it, robotRecipes)) {
            indexesToRemove.add(index)
        }
    }

    val statesList = possibleStates.toMutableList()
    indexesToRemove.reversed().forEach {
        statesList.removeAt(it)
    }

    possibleStates.clear()
    possibleStates.addAll(statesList)
}

fun getMaxGeodeProduction(state: State, minutesLeft: Int): Int {
    var maxMiningSpeed = state.robots.geodeSpeed
    var newResources = 0
    
    for (i in 1..minutesLeft) {
        newResources += maxMiningSpeed
        maxMiningSpeed++
    }
    
    return newResources
}

fun removeNonsenseStates2(possibleStates: MutableSet<State>) {
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
                }
            }
        }
    }

    val statesList = possibleStates.toMutableList()
    indexesToRemove.reversed().forEach {
        statesList.removeAt(it)
    }

    possibleStates.clear()
    possibleStates.addAll(statesList)
}

fun spawnNewPossibleStates(possibleStates: MutableSet<State>, robotRecipes: List<RobotRecipe>, iteration: Int, maxOreCost: Int, maxClayCost: Int, maxObsidianCost: Int) {
    val newStates = mutableSetOf<State>()
    
    possibleStates.forEach { 
        if (robotRecipes[3].oreCost <= it.resources.ore && robotRecipes[3].obsidianCost <= it.resources.obsidian  && it.allowedToBuild.contains("Geode")) {
            it.resources.ore -= robotRecipes[3].oreCost
            it.resources.obsidian -= robotRecipes[3].obsidianCost
            if (iteration != 24) { it.resources.geode-- } else { it.resources.geode }
            it.robots.geodeSpeed++
            it.allowedToBuild = mutableListOf("Ore", "Clay", "Obsidian", "Geode")
        } else {
            if (robotRecipes[0].oreCost <= it.resources.ore && it.allowedToBuild.contains("Ore") && it.robots.oreSpeed < maxOreCost) {
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
                    ),
                    mutableListOf("Ore", "Clay", "Obsidian", "Geode")
                )
                newStates.add(newState)
            }

            if (robotRecipes[1].oreCost <= it.resources.ore && it.allowedToBuild.contains("Clay") && it.robots.claySpeed < maxClayCost) {
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
                    ),
                    mutableListOf("Ore", "Clay", "Obsidian", "Geode")
                )
                newStates.add(newState)
            }

            if (robotRecipes[2].oreCost <= it.resources.ore && robotRecipes[2].clayCost <= it.resources.clay && it.allowedToBuild.contains("Obsidian") && it.robots.obsidianSpeed < maxObsidianCost) {
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
                    ),
                    mutableListOf("Ore", "Clay", "Obsidian", "Geode")
                )
                newStates.add(newState)
            }

            /*if (robotRecipes[3].oreCost <= it.resources.ore && robotRecipes[3].obsidianCost <= it.resources.obsidian && it.allowedToBuild.contains("Geode")) {
                val newState = State(
                    Resources(
                        it.resources.ore - robotRecipes[3].oreCost,
                        it.resources.clay,
                        it.resources.obsidian - robotRecipes[3].obsidianCost,
                        it.resources.geode - 1
                    ),
                    Robot(
                        it.robots.oreSpeed,
                        it.robots.claySpeed,
                        it.robots.obsidianSpeed,
                        it.robots.geodeSpeed + 1
                    ),
                    mutableListOf("Ore", "Clay", "Obsidian", "Geode")
                )
                newStates.add(newState)
            }*/

            val allowedBuys = mutableListOf("Ore", "Clay", "Obsidian", "Geode")

            if (canAffordOre(it, robotRecipes)) {
                allowedBuys.remove("Ore")
            }

            if (canAffordClay(it, robotRecipes)) {
                allowedBuys.remove("Clay")
            }

            if (canAffordObsidian(it, robotRecipes)) {
                allowedBuys.remove("Obsidian")
            }

            if (canAffordGeode(it, robotRecipes)) {
                allowedBuys.remove("Geode")
            }

            it.allowedToBuild = allowedBuys
        }
    }
        
    possibleStates.addAll(newStates)
}

fun canAffordAllRobots(state: State, robotRecipes: List<RobotRecipe>): Boolean {
    if (canAffordOre(state, robotRecipes) && canAffordClay(state, robotRecipes) && canAffordObsidian(state, robotRecipes) && canAffordGeode(state, robotRecipes)) {
        return true
    }
    
    return false
}

fun canAffordAllAvailableRobots(state: State, robotRecipes: List<RobotRecipe>): Boolean {
    if (state.robots.claySpeed == 0 && canAffordOre(state, robotRecipes) && canAffordClay(state, robotRecipes)) {
        return true
    }
    
    return false
}

fun canAffordAllAvailableRobots2(state: State, robotRecipes: List<RobotRecipe>): Boolean {
    if (state.robots.obsidianSpeed == 0 && canAffordOre(state, robotRecipes) && canAffordClay(state, robotRecipes) && canAffordObsidian(state, robotRecipes)) { 
        return true
    }

    return false
}

fun canAffordOre(state: State, robotRecipes: List<RobotRecipe>): Boolean {
    return robotRecipes[0].oreCost <= state.resources.ore
}

fun canAffordClay(state: State, robotRecipes: List<RobotRecipe>): Boolean {
    return robotRecipes[1].oreCost <= state.resources.ore
}

fun canAffordObsidian(state: State, robotRecipes: List<RobotRecipe>): Boolean {
    return robotRecipes[2].oreCost <= state.resources.ore && robotRecipes[2].clayCost <= state.resources.clay
}

fun canAffordGeode(state: State, robotRecipes: List<RobotRecipe>): Boolean {
    return robotRecipes[3].oreCost <= state.resources.ore && robotRecipes[3].obsidianCost <= state.resources.obsidian
}

fun increaseResources(possibleStates: Set<State>) {
    possibleStates.forEach { 
        it.resources.ore += it.robots.oreSpeed
        it.resources.clay += it.robots.claySpeed
        it.resources.obsidian += it.robots.obsidianSpeed
        it.resources.geode += it.robots.geodeSpeed
    }
}

data class RobotRecipe(val produces: Robot, val oreCost: Int, val clayCost: Int, val obsidianCost: Int)

data class Robot(var oreSpeed: Int, var claySpeed: Int, var obsidianSpeed: Int, var geodeSpeed: Int)

data class Resources(var ore: Int, var clay: Int, var obsidian: Int, var geode: Int)

data class State(val resources: Resources, var robots: Robot, var allowedToBuild: MutableList<String>)