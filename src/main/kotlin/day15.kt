import kotlin.math.abs

const val Y_ROW = 2000000
const val MAX_VALUE = Y_ROW * 2

fun day15(lines: List<String>) {
    val sensors = findBeacons(lines)
    val xRange = findXRange(sensors)
    val noBeaconPositions = findNoBeaconPositions(sensors, xRange)
    val beaconsOnRowOfInterest = findBeaconsOnRowOfInterest(sensors)
    
    //val distressBeacon = findDistressBeacon(sensors)
    
    println("Day 15 part 1: ${noBeaconPositions - beaconsOnRowOfInterest}")
    //println("Day 15 part 2: ${distressBeacon.x * MAX_VALUE + distressBeacon.y}")
}

fun findDistressBeacon(sensors: List<Sensor>): Position {
    for (x in 0 .. MAX_VALUE) {
        for (y in 0 .. MAX_VALUE) {
            var noBeaconHere = false
            run breaking@{
                sensors.forEach {
                    if (getManhattanDistance(it.position, Position(x, y)) <= it.closestBeaconDistance) {
                        noBeaconHere = true
                        return@breaking
                    }
                }
            }

            if (!noBeaconHere) {
                return Position(x, y)
            }
        }
    }
    
    return Position(0,0)
}

fun findBeaconsOnRowOfInterest(sensors: List<Sensor>): Int {
    return sensors
        .map { it.closestBeacon }
        .filter { it.y == Y_ROW }
        .toSet()
        .size
}

fun findNoBeaconPositions(sensors: List<Sensor>, xRange: IntRange): Int {
    var noBeaconPositions = 0
    for (i in xRange) {
        var noBeaconHere = false
        run breaking@{
            sensors.forEach {
                if (getManhattanDistance(it.position, Position(i, Y_ROW)) <= it.closestBeaconDistance) {
                    noBeaconHere = true
                    return@breaking
                }
            }
        }
        if (noBeaconHere) {
            noBeaconPositions++
        }
    }
    
    return noBeaconPositions
}

fun findXRange(sensors: List<Sensor>): IntRange {
    return sensors.map { it.closestBeacon.x }.min() - Y_ROW.. sensors.map { it.closestBeacon.x }.max() + Y_ROW
}

fun findBeacons(lines: List<String>): List<Sensor> {
    val sensors = mutableListOf<Sensor>()
    lines.forEach { 
        val line = it.replace("Sensor at x=", "").replace(" closest beacon is at x=", "").replace(" y=", "")
        val sensorPos = Position(Integer.parseInt(line.split(":")[0].split(",")[0]), Integer.parseInt(line.split(":")[0].split(",")[1]))
        val beaconPos = Position(Integer.parseInt(line.split(":")[1].split(",")[0]), Integer.parseInt(line.split(":")[1].split(",")[1]))
        
        sensors.add(Sensor(sensorPos, beaconPos, getManhattanDistance(sensorPos, beaconPos)))        
    }
    
    return sensors
}

fun getManhattanDistance(left: Position, right: Position): Int {
    return abs(left.x - right.x) + abs(left.y - right.y)
}

data class Position(val x: Int, val y: Int)

data class Sensor(val position: Position, val closestBeacon: Position, val closestBeaconDistance: Int)