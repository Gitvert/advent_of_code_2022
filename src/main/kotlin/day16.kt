fun day16(lines: List<String>) {
    val valves = parseValves(lines)
    val maxPossibleFlow = valves.values.map { it.flowRate }.sum()
    
    val potentialStatesPart1 = mutableSetOf(ValveState(
        0, 
        0, 
        valves["AA"]!!.name, 
        "AA", 
        mutableSetOf(), 
        0, 
        mutableMapOf()
    ))

    for (i in 1..30) {
        potentialStatesPart1.forEach {
            it.totalPressureReleased += it.totalFlowRate
        }
        
        takeActionAlone(potentialStatesPart1, valves, maxPossibleFlow)
        removeSoloBadStates(potentialStatesPart1)
    }
    
    val part1Answer = potentialStatesPart1.maxOf { it.totalPressureReleased }

    println("Day 16 part 1: $part1Answer")
    
    val potentialStatesPart2 = mutableSetOf(ValveDuoState(
        0,
        0,
        valves["AA"]!!.name,
        valves["AA"]!!.name,
        "AA",
        "AA",
        mutableSetOf(),
        0,
        0,
        mutableMapOf("AA" to 1)
    ))
    
    for (i in 1..26) {
        potentialStatesPart2.forEach {
            it.totalPressureReleased += it.totalFlowRate
        }
        
        if (i < 26) { 
            takeActionTogether(potentialStatesPart2, valves, maxPossibleFlow)
            removeDuoBadStates(potentialStatesPart2, maxPossibleFlow, 26 - i)
        }
    }

    val part2Answer = potentialStatesPart2.maxOf { it.totalPressureReleased }

    println("Day 16 part 2: $part2Answer")
}

fun takeActionTogether(potentialStates: MutableSet<ValveDuoState>, valves: Map<String, Valve>, maxPossibleFlow: Int) {
    val newStates = mutableSetOf<ValveDuoState>()

    potentialStates.forEach { state ->
        if (state.totalFlowRate == maxPossibleFlow) {
            newStates.add(
                ValveDuoState(
                    state.totalFlowRate,
                    state.totalPressureReleased,
                    state.playerCurrentValve,
                    state.elephantCurrentValve,
                    state.playerMoveOrder,
                    state.elephantMoveOrder,
                    state.openValves,
                    state.playerLoopCounter,
                    state.elephantLoopCounter,
                    state.neighborsVisited.toMutableMap(),
                )
            )
        } else {
            val currentPlayerValve = valves[state.playerCurrentValve]!!
            val currentElephantValve = valves[state.elephantCurrentValve]!!
            
            var newStatePlayerOpensValve: ValveToOpen? = null
            var newStateElephantOpensValve: ValveToOpen? = null
            
            if (!state.openValves.contains(currentPlayerValve.name) && currentPlayerValve.flowRate > 0) {
                newStatePlayerOpensValve = getOpenValveState(currentPlayerValve)
            }

            if (!state.openValves.contains(currentElephantValve.name) && currentElephantValve.flowRate > 0 && currentElephantValve.name != currentPlayerValve.name) {
                newStateElephantOpensValve = getOpenValveState(currentElephantValve)
            }
            
            if (newStatePlayerOpensValve != null && newStateElephantOpensValve != null) {
                newStates.add(
                    ValveDuoState(
                        state.totalFlowRate + newStatePlayerOpensValve.flowRateIncrease + newStateElephantOpensValve.flowRateIncrease,
                        state.totalPressureReleased,
                        state.playerCurrentValve,
                        state.elephantCurrentValve,
                        state.playerMoveOrder,
                        state.elephantMoveOrder,
                        state.openValves.plus(newStatePlayerOpensValve.valveName).plus(newStateElephantOpensValve.valveName).toMutableSet(),
                        state.playerLoopCounter,
                        state.elephantLoopCounter,
                        state.neighborsVisited.toMutableMap(),
                    )
                )
            }

            val playerNeighbours = currentPlayerValve.neighbours
            val elephantNeighbors = currentElephantValve.neighbours

            playerNeighbours.forEach { playerNeighbour ->
                val playerPrevVisited = if (state.playerMoveOrder.length > 3) {
                    state.playerMoveOrder.reversed().subSequence(2, 4).reversed()
                } else {
                    " "
                }.toString()

                elephantNeighbors.forEach { elephantNeighbour ->
                    val elephantPrevVisited = if (state.elephantMoveOrder.length > 3) {
                        state.elephantMoveOrder.reversed().subSequence(2, 4).reversed()
                    } else {
                        " "
                    }.toString()
                    var neighborsVisited = state.neighborsVisited.toMutableMap()
                    
                    
                    if (neighborsVisited.containsKey(elephantNeighbour.name)) {
                        neighborsVisited[elephantNeighbour.name] = neighborsVisited[elephantNeighbour.name]!! + 1
                    } else {
                        neighborsVisited[elephantNeighbour.name] = 1
                    }

                    if (neighborsVisited.containsKey(playerNeighbour.name)) {
                        neighborsVisited[playerNeighbour.name] = neighborsVisited[playerNeighbour.name]!! + 1
                    } else {
                        neighborsVisited[playerNeighbour.name] = 1
                    }

                    newStates.add(
                        ValveDuoState(
                            state.totalFlowRate,
                            state.totalPressureReleased,
                            playerNeighbour.name,
                            elephantNeighbour.name,
                            state.playerMoveOrder + playerNeighbour.name,
                            state.elephantMoveOrder + elephantNeighbour.name,
                            state.openValves,
                            if (playerPrevVisited == playerNeighbour.name) {
                                state.playerLoopCounter + 1
                            } else {
                                0
                            },
                            if (elephantPrevVisited == elephantNeighbour.name) {
                                state.playerLoopCounter + 1
                            } else {
                                0
                            },
                            neighborsVisited
                        )
                    )
                    
                    if (newStatePlayerOpensValve != null) {
                        neighborsVisited = state.neighborsVisited.toMutableMap()

                        if (neighborsVisited.containsKey(elephantNeighbour.name)) {
                            neighborsVisited[elephantNeighbour.name] = neighborsVisited[elephantNeighbour.name]!! + 1
                        } else {
                            neighborsVisited[elephantNeighbour.name] = 1
                        }
                        
                        newStates.add(
                            ValveDuoState(
                                state.totalFlowRate + newStatePlayerOpensValve.flowRateIncrease,
                                state.totalPressureReleased,
                                state.playerCurrentValve,
                                elephantNeighbour.name,
                                state.playerMoveOrder,
                                state.elephantMoveOrder + elephantNeighbour.name,
                                state.openValves.plus(newStatePlayerOpensValve.valveName).toMutableSet(),
                                state.playerLoopCounter,
                                if (elephantPrevVisited == elephantNeighbour.name) {
                                    state.playerLoopCounter + 1
                                } else {
                                    0
                                },
                                neighborsVisited
                            )
                        )
                    }
                    
                    if (newStateElephantOpensValve != null) {
                        neighborsVisited = state.neighborsVisited.toMutableMap()

                        if (neighborsVisited.containsKey(playerNeighbour.name)) {
                            neighborsVisited[playerNeighbour.name] = neighborsVisited[playerNeighbour.name]!! + 1
                        } else {
                            neighborsVisited[playerNeighbour.name] = 1
                        }
                        
                        newStates.add(
                            ValveDuoState(
                                state.totalFlowRate + newStateElephantOpensValve.flowRateIncrease,
                                state.totalPressureReleased,
                                playerNeighbour.name,
                                state.elephantCurrentValve,
                                state.playerMoveOrder + playerNeighbour.name,
                                state.elephantMoveOrder,
                                state.openValves.plus(newStateElephantOpensValve.valveName).toMutableSet(),
                                if (playerPrevVisited == playerNeighbour.name) {
                                    state.playerLoopCounter + 1
                                } else {
                                    0
                                },
                                state.elephantLoopCounter,
                                neighborsVisited
                            )
                        )
                    }
                }
            }
        }
    }

    potentialStates.clear()
    potentialStates.addAll(newStates)
}

fun getOpenValveState(currentValve: Valve): ValveToOpen {
    return ValveToOpen(currentValve.flowRate, currentValve.name)
}

fun removeDuoBadStates(potentialStates: MutableSet<ValveDuoState>, maxPossibleFlow: Int, minutesLeft: Int) {
    val bestFlowRate = potentialStates.maxOf { it.totalFlowRate }
    val bestPressureReleased = potentialStates.maxOf { it.totalPressureReleased }
    val flowRateFromBestPressureReleased = potentialStates.maxBy { it.totalPressureReleased }.totalFlowRate

    val indexesToRemove = mutableSetOf<Int>()

    potentialStates.toList().forEachIndexed { index, it ->
        if (it.playerLoopCounter > 1 || it.elephantLoopCounter > 1) { //Remove states walking in loops
            indexesToRemove.add(index)
        }
        
        if (it.totalFlowRate + 35 <= bestFlowRate) { // Remove states with bad flow rate
            indexesToRemove.add(index)
        }

        if (it.neighborsVisited.values.max() > 3) { // Remove states that visited the same node too many times
            indexesToRemove.add(index)
        }
        
        if (it.playerMoveOrder.length > 3 && it.playerMoveOrder == it.elephantMoveOrder) { // Remove states that walked the same path
            indexesToRemove.add(index)
        }
        
        if (it.totalPressureReleased + 100 < bestPressureReleased) { // Remove states where pressure released is very far behind
            indexesToRemove.add(index)
        }
        
        if (minutesLeft < 6) {
            if (it.totalPressureReleased < bestPressureReleased) {
                val worstFromOtherState = bestPressureReleased + (flowRateFromBestPressureReleased * minutesLeft)
                val bestFromThisState = it.totalPressureReleased + (maxPossibleFlow * minutesLeft)
                
                if (bestFromThisState < worstFromOtherState) {
                    indexesToRemove.add(index)
                }
            }
        }
    }

    val statesList = potentialStates.toMutableList()
    indexesToRemove.reversed().forEach {
        statesList.removeAt(it)
    }

    potentialStates.clear()
    potentialStates.addAll(statesList)
}

fun takeActionAlone(potentialStates: MutableSet<ValveState>, valves: Map<String, Valve>, maxPossibleFlow: Int) {
    val newStates = mutableSetOf<ValveState>()
    
    potentialStates.forEach { state ->
        val currentValve = valves[state.currentValve]!!

        if (state.totalFlowRate == maxPossibleFlow) {
            newStates.add(
                ValveState(
                    state.totalFlowRate,
                    state.totalPressureReleased,
                    state.currentValve,
                    state.moveOrder,
                    state.openValves,
                    state.loopCounter,
                    state.neighborsVisited.toMutableMap()
                )
            )
        } else {
            if (!state.openValves.contains(currentValve.name) && currentValve.flowRate > 0) {
                newStates.add(
                    ValveState(
                        state.totalFlowRate + currentValve.flowRate,
                        state.totalPressureReleased,
                        state.currentValve,
                        state.moveOrder,
                        state.openValves.plus(currentValve.name).toMutableSet(),
                        state.loopCounter,
                        state.neighborsVisited.toMutableMap()
                    )
                )
            }

            val neighbours = currentValve.neighbours

            neighbours.forEach { neighbour ->
                val prevVisited = if (state.moveOrder.length > 3) {
                    state.moveOrder.reversed().subSequence(2, 4).reversed()
                } else {
                    " "
                }.toString()
                val neighborsVisited = state.neighborsVisited.toMutableMap()
                if (neighborsVisited.containsKey(neighbour.name)) {
                    neighborsVisited[neighbour.name] = neighborsVisited[neighbour.name]!! + 1
                } else {
                    neighborsVisited[neighbour.name] = 1
                }

                newStates.add(
                    ValveState(
                        state.totalFlowRate,
                        state.totalPressureReleased,
                        neighbour.name,
                        state.moveOrder + neighbour.name,
                        state.openValves,
                        if (prevVisited == neighbour.name) {
                            state.loopCounter + 1
                        } else {
                            0
                        },
                        neighborsVisited
                    )
                )
            }
        }
    }
    
    potentialStates.clear()
    potentialStates.addAll(newStates)
}

fun removeSoloBadStates(potentialStates: MutableSet<ValveState>) {
    val bestFlowRate = potentialStates.maxOf { it.totalFlowRate }
    
    val indexesToRemove = mutableSetOf<Int>()
    
    potentialStates.toList().forEachIndexed { index, it ->
        if (it.loopCounter > 1 || (it.totalFlowRate + 30 <= bestFlowRate)) {
            indexesToRemove.add(index)
        }
        
        if (it.neighborsVisited.values.max() > 3 ) {
            indexesToRemove.add(index)
        }
    }
    
    val statesList = potentialStates.toMutableList()
    indexesToRemove.reversed().forEach { 
        statesList.removeAt(it)
    }
    
    potentialStates.clear()
    potentialStates.addAll(statesList)
}

fun parseValves(lines: List<String>): Map<String, Valve> {
    val valves = mutableMapOf<String, Valve>()
    
    lines.forEach { 
        valves[it.substring(6,8)] = (Valve(
            it.substring(6,8), 
            it.split("=")[1].split(";")[0].toInt(),
            mutableListOf()
        ))
    }
    
    lines.forEachIndexed { index, line ->
        val neighbours = line.replace("valves", "valve").split("valve ")[1].split(", ")
               
        neighbours.forEach { n -> 
            val neighbour = valves[n]!!
            valves[line.substring(6,8)]!!.neighbours.add(neighbour)
        }
    }
    
    return valves
}

data class Valve(val name: String, val flowRate: Int, val neighbours: MutableList<Valve>)

data class ValveState(
    var totalFlowRate: Int, 
    var totalPressureReleased: Int, 
    var currentValve: String, 
    var moveOrder: String, 
    val openValves: MutableSet<String>, 
    var loopCounter: Int, 
    val neighborsVisited: MutableMap<String, Int>
)

data class ValveDuoState(
    var totalFlowRate: Int,
    var totalPressureReleased: Int,
    var playerCurrentValve: String,
    var elephantCurrentValve: String,
    var playerMoveOrder: String,
    var elephantMoveOrder: String,
    val openValves: MutableSet<String>,
    var playerLoopCounter: Int,
    var elephantLoopCounter: Int,
    val neighborsVisited: MutableMap<String, Int>,
)

data class ValveToOpen(val flowRateIncrease: Int, val valveName: String)