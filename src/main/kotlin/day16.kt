fun day16(lines: List<String>) {
    val valves = parseValves(lines)
    val valvesToOpenCount = valves.values.map { it.flowRate }.filter { it > 0 }.size
    
    val startState = ValveState(0, 0, valves["AA"]!!.name, "AA", mutableSetOf(), 0, mutableMapOf())
    
    val potentialStates = mutableSetOf(startState)

    for (i in 1..30) {
        println("Starting minute $i")
        potentialStates.forEach {
            it.totalPressureReleased += it.totalFlowRate
        }
        takeAction(potentialStates, valves, valvesToOpenCount)
        removeBadStates(potentialStates)
    }
    
    val answer = potentialStates.maxOf { it.totalPressureReleased }
    
    println("Day 16 part 1: $answer")
}

fun takeAction(potentialStates: MutableSet<ValveState>, valves: Map<String, Valve>, valvesToOpenCount: Int) {
    val newStates = mutableSetOf<ValveState>()
    
    potentialStates.forEach { state ->
        val currentValve = valves[state.currentValve]!!

        if (state.openValves.size == valvesToOpenCount) {
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

fun removeBadStates(potentialStates: MutableSet<ValveState>) {
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

fun parseValves(lines: List<String>): MutableMap<String, Valve> {
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

data class Valve(val name: String, val flowRate: Int, val neighbours: MutableList<Valve>) {
    override fun hashCode(): Int {
        return name.hashCode() + flowRate.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) { return true }
        if (other!!.javaClass != javaClass) { return false }
        
        other as Valve

        return name == other.name && flowRate == other.flowRate
    }
}

data class ValveState(var totalFlowRate: Int, var totalPressureReleased: Int, var currentValve: String, var moveOrder: String, val openValves: MutableSet<String>, var loopCounter: Int, val neighborsVisited: MutableMap<String, Int>)