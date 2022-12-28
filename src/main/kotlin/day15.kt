import kotlin.math.abs

const val Y_ROW = 2000000L
const val MAX_VALUE = Y_ROW * 2

fun day15(lines: List<String>) {
    val sensors = findSensors(lines)
    val xRange = findXRange(sensors)

    val answer = findNoBeaconPositions(sensors, xRange) - findBeaconsOnRowOfInterest(sensors)

    println("Day 15 part 1: $answer")

    val answer2 = findDistressBeacon(sensors)

    println("Day 15 part 2: ${answer2.x * MAX_VALUE + answer2.y}")
}

fun findDistressBeacon(sensors: List<Sensor>): Point {
    sensors.forEach { sensor ->
        val point = Point(sensor.position.x, sensor.position.y + sensor.closestBeaconDistance + 1)
        
        val answer = walkDownRight(sensor, point, sensors) ?: walkDownLeft(sensor, point, sensors) ?: walkUpLeft(sensor, point, sensors) ?: walkUpRight(sensor, point, sensors)
        
        if (answer != null) {
            return answer
        }
    }

    return Point(0, 0)
}

fun walkDownRight(sensor: Sensor, point: Point, sensors: List<Sensor>): Point? {
    while (true) {
        point.x++
        point.y--

        if (point.y == sensor.position.y || pointOutSideSearchArea(point)) {
            break
        }

        val answer = findDetectingSensors(sensors, point)
        if (answer != null) {
            return answer
        }
    }
    
    return null
}

fun walkDownLeft(sensor: Sensor, point: Point, sensors: List<Sensor>): Point? {
    while (true) {
        point.x--
        point.y--

        if (point.x == sensor.position.x || pointOutSideSearchArea(point)) {
            break
        }

        val answer = findDetectingSensors(sensors, point)
        if (answer != null) {
            return answer
        }
    }
    
    return null
}

fun walkUpLeft(sensor: Sensor, point: Point, sensors: List<Sensor>): Point? {
    while (true) {
        point.x--
        point.y++

        if (point.y == sensor.position.y || pointOutSideSearchArea(point)) {
            break
        }

        val answer = findDetectingSensors(sensors, point)
        if (answer != null) {
            return answer
        }
    }
    
    return null
}

fun walkUpRight(sensor: Sensor, point: Point, sensors: List<Sensor>): Point? {
    while (true) {
        point.x++
        point.y++

        if (point.x == sensor.position.x || pointOutSideSearchArea(point)) {
            break
        }

        val answer = findDetectingSensors(sensors, point)
        if (answer != null) {
            return answer
        }
    }
    
    return null
}

fun findDetectingSensors(sensors: List<Sensor>, point: Point): Point? {
    var notPossible = 0
    
    run breaking@{
        notPossible = 0
        sensors.forEach { innerSensor ->
            if (getManhattanDistance(innerSensor.position, point) <= innerSensor.closestBeaconDistance) {
                notPossible++
                return@breaking
            }
        }
        if (notPossible == 0) {
            return point
        }
    }
    
    return null
}

fun pointOutSideSearchArea(point: Point): Boolean {
    return point.x < 0 || point.y < 0 || point.x > MAX_VALUE || point.y > MAX_VALUE
}

fun findBeaconsOnRowOfInterest(sensors: List<Sensor>): Int {
    return sensors
        .map { it.closestBeacon }
        .filter { it.y == Y_ROW }
        .toSet()
        .size
}

fun findNoBeaconPositions(sensors: List<Sensor>, xRange: LongRange): Int {
    var noBeaconPositions = 0
    for (i in xRange) {
        var noBeaconHere = false
        run breaking@{
            sensors.forEach {
                if (getManhattanDistance(it.position, Point(i, Y_ROW)) <= it.closestBeaconDistance) {
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

fun findSensors(lines: List<String>): List<Sensor> {
    val sensors = mutableListOf<Sensor>()
    lines.forEach {
        val line = it.replace("Sensor at x=", "").replace(" closest beacon is at x=", "").replace(" y=", "")
        val sensorPos = Point(line.split(":")[0].split(",")[0].toLong(), line.split(":")[0].split(",")[1].toLong())
        val beaconPos = Point(line.split(":")[1].split(",")[0].toLong(), line.split(":")[1].split(",")[1].toLong())
        
        val sensor = Sensor(sensorPos, beaconPos, getManhattanDistance(sensorPos, beaconPos))
        sensors.add(sensor)
    }

    return sensors
}

fun findXRange(sensors: List<Sensor>): LongRange {
    return sensors.minOf { it.closestBeacon.x } - 1500000L..sensors.maxOf { it.closestBeacon.x } + 500000L
}

fun getManhattanDistance(left: Point, right: Point): Long {
    return abs(left.x - right.x) + abs(left.y - right.y)
}

data class Point(var x: Long, var y: Long)

data class Sensor(
    val position: Point,
    val closestBeacon: Point,
    val closestBeaconDistance: Long
)
