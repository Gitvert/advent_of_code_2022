fun day21(lines: List<String>) {
    val monkeyJobs = getMonkeyJobs(lines)
    updateNumbers(monkeyJobs)
    
    println("Day 21 part 1: ${monkeyJobs["root"]!!.number}")
    
    val correctMonkeyJobs = getMonkeyJobs(lines)
    correctMonkeyJobs["root"]!!.operation = '='
    correctMonkeyJobs["humn"]!!.number = null
    updateNumbers(correctMonkeyJobs)
    fillDiscoveredNumbers(correctMonkeyJobs)
    
    println("Day 21 part 2: ${findEquation(correctMonkeyJobs)}")
}

fun findEquation(monkeyJobs: MutableMap<String, MonkeyJob>): String {
    var current = monkeyJobs["root"]!!
    var equation = "${current.left} ${current.operation} ${current.right}"
    
    while (true) {
        val left = current.left
        val right = current.right
        
        if (left != null && left.all { it.isDigit() }) {
            val monkeyJob = monkeyJobs[right]!!
            equation = equation.replace(right!!, "(${monkeyJob.left} ${monkeyJob.operation} ${monkeyJob.right})")
            current = monkeyJob
        }
        
        if (right != null && right.all { it.isDigit() }) {
            val monkeyJob = monkeyJobs[left]!!
            equation = equation.replace(left!!, "(${monkeyJob.left} ${monkeyJob.operation} ${monkeyJob.right})")
            current = monkeyJob
        }
        
        if (equation.contains("humn")) {
            equation = equation.replace("humn", "X")
            break
        }
    }
    
    return equation
}

fun fillDiscoveredNumbers(monkeyJobs: MutableMap<String, MonkeyJob>) {
    monkeyJobs.forEach {
        val leftNumber = monkeyJobs[it.value.left]?.number
        val rightNumber = monkeyJobs[it.value.right]?.number
        
        if (leftNumber != null) {
            it.value.left = leftNumber.toString()
        }
        
        if (rightNumber != null) {
            it.value.right = rightNumber.toString()
        }
    }
}

fun updateNumbers(monkeyJobs: MutableMap<String, MonkeyJob>) {
    var foundNumbers = 0
    var lastFoundNumbers = -1
    val maxNumbers = monkeyJobs.entries.size
    
    while (foundNumbers < maxNumbers) {
        if (lastFoundNumbers == foundNumbers) {
            return
        }
        lastFoundNumbers = foundNumbers
        foundNumbers = 0
        monkeyJobs.forEach {
            if (it.value.number != null) {
                foundNumbers++
            } else {
                if (tryToCalculateNumber(it.value, monkeyJobs)) {
                    foundNumbers++
                }
            }
        }
    }
}

fun tryToCalculateNumber(monkeyJob: MonkeyJob, monkeyJobs: MutableMap<String, MonkeyJob>): Boolean {
    val leftNumber = monkeyJobs[monkeyJob.left]?.number
    val rightNumber = monkeyJobs[monkeyJob.right]?.number
    
    if (leftNumber != null && rightNumber != null) {
        when (monkeyJob.operation) {
            '+' -> monkeyJob.number = leftNumber + rightNumber
            '-' -> monkeyJob.number = leftNumber - rightNumber
            '*' -> monkeyJob.number = leftNumber * rightNumber
            '/' -> monkeyJob.number = leftNumber / rightNumber
        }
        
        return true
    }
    
    return false
}

fun getMonkeyJobs(lines: List<String>): MutableMap<String, MonkeyJob> {
    val monkeyJobs = mutableMapOf<String, MonkeyJob>()

    lines.forEach {
        val monkeyJob = if (it[6].isDigit()) {
            MonkeyJob(it.split(": ")[1].toLong(), null, null, null)
        } else {
            MonkeyJob(null, it.substring(6..9), it[11], it.substring(13..16))
        }

        monkeyJobs[it.substring(0..3)] = monkeyJob
    }
    
    return monkeyJobs
}

data class MonkeyJob(var number: Long?, var left: String?, var operation: Char?, var right: String?)