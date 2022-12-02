fun day1(lines: List<String>) {
    var currentCalories = 0
    val calorieList = mutableListOf<Int>()

    lines.forEach {
        if (it.isEmpty()) {
            calorieList.add(currentCalories)
            currentCalories = 0
        } else {
            currentCalories += Integer.valueOf(it)
        }
    }

    calorieList.sortDescending()
    println("Day 1 part 1: " + calorieList[0])
    println("Day 1 part 2: " + (calorieList[0] + calorieList[1] + calorieList[2]))
    println()
}