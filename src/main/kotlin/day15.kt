import kotlin.math.abs

const val Y_ROW = 2000000L
const val MAX_VALUE = Y_ROW * 2

fun day15(lines: List<String>) {
    val sensors = findSensors(lines)
    val xRange = findXRange(sensors)

    val answer = findNoBeaconPositions(sensors, xRange)

    println("Day 15 part 1: $answer")

    val answer2 = findDistressBeacon(sensors)

    println("Day 15 part 2: ${answer2.x * MAX_VALUE + answer2.y}")
}

fun findDistressBeacon(sensors: List<Sensor>): Point {
    var notPossible = 0

    sensors.forEach { sensor ->
        sensor.perimeterPoints.forEach { point ->
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
        }
    }

    return Point(0, 0)
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

fun computePerimeterPoints(sensor: Sensor) {
    val points = mutableListOf<Point>()
    var point = Point(sensor.position.x, sensor.position.y + sensor.closestBeaconDistance + 1)

    points.add(point)
    while (true) { // Walk down right
        point = Point(point.x + 1, point.y - 1)
        points.add(point)

        if (point.y == sensor.position.y) {
            break
        }
    }

    while (true) { // Walk down left
        point = Point(point.x - 1, point.y - 1)
        points.add(point)

        if (point.x == sensor.position.x) {
            break
        }
    }

    while (true) { // Walk up left
        point = Point(point.x - 1, point.y + 1)
        points.add(point)

        if (point.y == sensor.position.y) {
            break
        }
    }

    while (true) { // Walk up right
        point = Point(point.x + 1, point.y + 1)
        points.add(point)

        if (point.x == sensor.position.x) {
            break
        }
    }

    sensor.perimeterPoints.addAll(points.filter { it.x in 0..MAX_VALUE && it.y in 0..MAX_VALUE })
}

fun findSensors(lines: List<String>): List<Sensor> {
    val sensors = mutableListOf<Sensor>()
    lines.forEach {
        val line = it.replace("Sensor at x=", "").replace(" closest beacon is at x=", "").replace(" y=", "")
        val sensorPos = Point(line.split(":")[0].split(",")[0].toLong(), line.split(":")[0].split(",")[1].toLong())
        val beaconPos = Point(line.split(":")[1].split(",")[0].toLong(), line.split(":")[1].split(",")[1].toLong())
        
        val sensor = Sensor(sensorPos, beaconPos, getManhattanDistance(sensorPos, beaconPos), mutableListOf())
        computePerimeterPoints(sensor)
        sensors.add(sensor)
    }

    return sensors
}

fun findXRange(sensors: List<Sensor>): LongRange {
    return sensors.minOf { it.closestBeacon.x } - 1500000L..sensors.maxOf { it.closestBeacon.x } + 1500000L
}

fun getManhattanDistance(left: Point, right: Point): Long {
    return abs(left.x - right.x) + abs(left.y - right.y)
}

data class Point(val x: Long, val y: Long)

data class Sensor(
    val position: Point,
    val closestBeacon: Point,
    val closestBeaconDistance: Long,
    val perimeterPoints: MutableList<Point>
)
