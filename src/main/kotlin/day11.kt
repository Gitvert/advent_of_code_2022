fun day11(lines: List<String>) {
    println("Day 11 part 1: ${solve(lines, 3L, 20)}")
    println("Day 11 part 2: ${solve(lines, 1L, 10000)}")
}

fun solve(lines: List<String>, divisor: Long, rounds: Int): Long {
    val monkeyList = createMoneyList(lines.filter { it.isNotEmpty() }.map { it.trim() })
    val activityList = mutableListOf<Long>()
    monkeyList.forEach { _ ->
        activityList.add(0L)
    }

    var limiter = 1

    monkeyList.forEach {
        limiter *= it.divisibleTest
    }

    for (i in 0 until rounds) {
        monkeyList.forEachIndexed { index, monkey ->
            monkey.items.forEach {
                val worryLevel = monkey.operation(it) / divisor % limiter

                if (worryLevel % monkey.divisibleTest == 0L) {
                    monkeyList[monkey.trueTarget].items.add(worryLevel)
                } else {
                    monkeyList[monkey.falseTarget].items.add(worryLevel)
                }
                activityList[index]++
            }
            monkey.items.clear()
        }
    }

    activityList.sortDescending()
    
    return activityList[0] * activityList[1]
}

fun createMoneyList(lines: List<String>): List<Monkey> {
    val monkeyList = mutableListOf<Monkey>()
    
    lines.forEachIndexed { index, it ->
        if (it.startsWith("Monkey")) {
            monkeyList.add(Monkey(
                lines[index + 1].split(": ")[1].split(", ").map { Integer.parseInt(it).toLong() }.toMutableList(),
                getOperation(lines[index + 2]),
                Integer.parseInt(lines[index + 3].split("by ")[1]),
                Integer.parseInt(lines[index + 4].split("monkey ")[1]),
                Integer.parseInt(lines[index + 5].split("monkey ")[1])
            ))
        }
    }
    
    return monkeyList
}

fun getOperation(operation: String): (Long) -> Long {
    if (operation.contains("old * old")) {
        return { worryLevel: Long -> worryLevel * worryLevel }
    } else if (operation.contains("*")) {
        val number = Integer.parseInt(operation.split("* ")[1]).toLong()
        return { worryLevel: Long -> worryLevel * number }
    } else {
        val number = Integer.parseInt(operation.split("+ ")[1]).toLong()
        return { worryLevel: Long -> worryLevel + number }
    }
}

data class Monkey(val items: MutableList<Long>, val operation: (Long) -> Long, val divisibleTest: Int, val trueTarget: Int, val falseTarget: Int)