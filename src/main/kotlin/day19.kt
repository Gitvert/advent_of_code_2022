fun day19(lines: List<String>) {
    var answerPart1 = 0

    lines.forEach { 
         answerPart1 += part1(it)
     }

    println("Day 19 part 1: $answerPart1")
    
    var answerPart2 = 1

    lines.take(3).forEach { 
        answerPart2 *= part2(it)
    }

    println("Day 19 part 2: $answerPart2")
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
        increaseResources(possibleStates)

        if (i < 24) {
            spawnNewPossibleStates(possibleStates, robotRecipes, i, maxOreCost, maxClayCost, maxObsidianCost)
        }
    }

    val maxGeodeCount = possibleStates.maxOf {
        it.resources.geode
    }
    
    return results[0] * maxGeodeCount
}

fun part2(line: String): Int {
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

    for (i in 1..32) {
        increaseResources(possibleStates)

        if (i < 32) {
            spawnNewPossibleStates(possibleStates, robotRecipes, i, maxOreCost, maxClayCost, maxObsidianCost)
        }
    }

    val maxGeodeCount = possibleStates.maxOf {
        it.resources.geode
    }

    return maxGeodeCount
}

fun spawnNewPossibleStates(possibleStates: MutableSet<State>, robotRecipes: List<RobotRecipe>, iteration: Int, maxOreCost: Int, maxClayCost: Int, maxObsidianCost: Int) {
    val newStates = mutableSetOf<State>()
    
    possibleStates.forEach { 
        if (robotRecipes[3].oreCost <= it.resources.ore && robotRecipes[3].obsidianCost <= it.resources.obsidian  && it.allowedToBuild.contains("Geode")) {
            it.resources.ore -= robotRecipes[3].oreCost
            it.resources.obsidian -= robotRecipes[3].obsidianCost
            it.resources.geode--
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