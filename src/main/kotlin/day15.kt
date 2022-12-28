import kotlin.math.abs

const val Y_ROW = 2000000L
const val MAX_VALUE = Y_ROW * 2

fun day15(lines: List<String>) {
    val sensors = findSensors(lines)
    val xRange = findXRange(sensors)
    computeExtraData(sensors)

    val answer = noOfNonBeaconPositions(sensors, xRange)

    println("Day 15 part 1: $answer")

    val answer2 = findDistressBeacon(sensors)

    println("Day 15 part 2: ${answer2.x * 4000000 + answer2.y}")
}

fun findDistressBeacon(sensors: List<Sensor>): Point {
    var notPossible = 0

    sensors.forEachIndexed { index, sensor ->
        sensor.perimeterPoints.forEach { point ->
            run breaking@{
                notPossible = 0
                sensors.forEach { innerSensor ->
                    if (pointIsInTriangle(point, innerSensor.bottomTriangle!!) || pointIsInTriangle(point, innerSensor.topTriangle!!)) {
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

fun noOfNonBeaconPositions(sensors: List<Sensor>, xRange: LongRange): Int {
    val noBeaconPositions = mutableSetOf<Point>()
    for (i in xRange) {
        sensors.forEach {
            if ((pointIsInTriangle(Point(i, Y_ROW), it.topTriangle!!) || pointIsInTriangle(Point(i, Y_ROW), it.bottomTriangle!!)) && Point(i, Y_ROW) != it.closestBeacon) {
                noBeaconPositions.add(Point(i, Y_ROW))
            }
        }
    }

    return noBeaconPositions.size
}

fun computeExtraData(sensors: List<Sensor>) {
    sensors.forEach {
        val distance = it.closestBeaconDistance

        it.topTriangle = Triangle(Point(it.position.x + distance, it.position.y), Point(it.position.x, it.position.y - distance), Point(it.position.x - distance, it.position.y))
        it.bottomTriangle = Triangle(Point(it.position.x + distance, it.position.y), Point(it.position.x, it.position.y + distance), Point(it.position.x - distance, it.position.y))

        computePerimeterPoints(it)
    }
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

        sensors.add(Sensor(sensorPos, beaconPos, getManhattanDistance(sensorPos, beaconPos), null, null, mutableListOf()))
    }

    return sensors
}

fun findXRange(sensors: List<Sensor>): LongRange {
    return sensors.minOf { it.closestBeacon.x } - 1500000L..sensors.maxOf { it.closestBeacon.x } + 1500000L
}

fun getManhattanDistance(left: Point, right: Point): Long {
    return abs(left.x - right.x) + abs(left.y - right.y)
}

fun pointIsInTriangle(point: Point, triangle: Triangle): Boolean {
    val a = triangleArea(triangle)

    val a1 = triangleArea(Triangle(point, triangle.b, triangle.c))

    val a2 = triangleArea(Triangle(triangle.a, point, triangle.c))

    val a3 = triangleArea(Triangle(triangle.a, triangle.b, point))
    Triangle(triangle.a, triangle.b, point)

    return (a == a1 + a2 + a3)
}

fun triangleArea(triangle: Triangle): Double {
    return abs((triangle.a.x*(triangle.b.y-triangle.c.y)+triangle.b.x*(triangle.c.y-triangle.a.y)+triangle.c.x*(triangle.a.y-triangle.b.y))/2.0)
}

data class Point(val x: Long, val y: Long)

data class Sensor(
    val position: Point,
    val closestBeacon: Point,
    val closestBeaconDistance: Long,
    var topTriangle: Triangle?,
    var bottomTriangle: Triangle?,
    val perimeterPoints: MutableList<Point>
)

data class Triangle(val a: Point, val b: Point, val c: Point)