import kotlin.math.min

fun day13(lines: List<String>) {
    val modifiedLines = lines.toMutableList()
    modifiedLines.add("[[6]]")
    modifiedLines.add("[[2]]")
    
    val pairs = getPairs(modifiedLines.filter { it.isNotEmpty() })
    
    val packetPairs = pairs
        .map { 
            Pair(
                convertToPacketData(it.first.drop(1).dropLast(1)),
                convertToPacketData(it.second.drop(1).dropLast(1))
            )
        }

    val allPackets = mutableListOf<PacketData>()
    
    packetPairs.forEach { 
        allPackets.add(it.first)
        allPackets.add(it.second)
    }
    
    println("Day 13 part 1: ${getIndexSum(packetPairs)}")
    println("Day 13 part 2: ${getDecoderKey(allPackets)}")
}

fun getDecoderKey(packets: MutableList<PacketData>): Int {
    sort(packets)
    
    var product = 1
    
    packets.forEachIndexed { index, it -> 
        if (it.stringRepresentation == "[6]" || it.stringRepresentation == "[2]") {
            product *= (index + 1)
        }
    }
    
    return product
}

fun sort(packets: MutableList<PacketData>) {
    var swap = true
    
    while (swap) {
        swap = false
        for (i in 0 until packets.size - 1) {
            if (!isInRightOrder(packets[i], packets[i+1])!!) {
                val temp = packets[i]
                packets[i] = packets[i+1]
                packets[i+1] = temp
                
                swap = true
            }
        }
    }
}

fun getIndexSum(pairs: List<Pair<PacketData, PacketData>>): Int {
    var sum = 0
    
    pairs.forEachIndexed { index, pair -> 
        if (isInRightOrder(pair.first, pair.second)!!) {
            sum += index + 1
        }
    }
    
    return sum
}

fun isInRightOrder(left: PacketData, right: PacketData): Boolean? {
    if (left.integer > -1 && right.integer > -1) { //Both are numbers
        if (left.integer < right.integer) {
            return true
        } else if (left.integer > right.integer) {
            return false
        } 
    } else if (left.integer == -1 && right.integer == -1) { //Both are lists
        val shortestList = min(left.list.size, right.list.size)
        for (i in 0 until shortestList) {
            val result = isInRightOrder(left.list[i], right.list[i])
            if (result != null) {
                return result
            }
        }

        if (left.list.size == shortestList && right.list.size != shortestList) {
            return true
        } else if (right.list.size == shortestList && left.list.size != shortestList) {
            return false
        }
        
    } else { //Mixed types
        if (left.integer > -1) {
            return isInRightOrder(PacketData(-1, mutableListOf(PacketData(left.integer, mutableListOf(), "")), ""), right)
        } else if (right.integer > -1) {
            return isInRightOrder(left, PacketData(-1, mutableListOf(PacketData(right.integer, mutableListOf(), "")), ""))
        }
    }    
    
    return null
}

fun convertToPacketData(line: String): PacketData {
    val current = PacketData(-1, mutableListOf(), line)
    
    var numberString = ""
    var stackDepth = 0
    var firstListStart = -1
    
    line.forEachIndexed { index, it ->
        if (it == '[') {
            stackDepth++
            if (firstListStart == -1) {
                firstListStart = index
            }
        } else if (it == ']') {
            stackDepth--
            if (stackDepth == 0) {
                current.list.add(convertToPacketData(line.substring(firstListStart + 1, index)))
                firstListStart = -1
            }
        } else if (firstListStart == -1 && it == ',') {
            if (numberString.isNotEmpty()) {
                current.list.add(PacketData(Integer.parseInt(numberString), mutableListOf(), ""))
                numberString = ""
            }
        } else if (firstListStart == -1) {
            numberString += it
        }
    }

    if (numberString.isNotEmpty()) {
        current.list.add(PacketData(Integer.parseInt(numberString), mutableListOf(), ""))
    }
    
    return current
}

fun getPairs(lines: List<String>): List<Pair<String, String>> {
    val pairList = mutableListOf<Pair<String, String>>()
    
    for (i in lines.indices step 2) {
        pairList.add(Pair(lines[i], lines[i+1]))
    }
    
    return pairList
}

data class PacketData (val integer: Int, val list: MutableList<PacketData>, val stringRepresentation: String)