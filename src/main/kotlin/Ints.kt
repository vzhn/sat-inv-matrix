
fun decodeInt(a: List<Variable>, assignments: Map<Variable, Boolean>): Int {
  val isNegative = assignments.getValue(a.last())
  var v = if (isNegative) -1 else 0
  val vs = mutableListOf<Boolean>()
  for (i in 0..<a.size - 1) {
    val value = assignments.getValue(a[i])
    vs.add(value)
    val bit = 1 shl i

    v = if (value) {
      v or bit
    } else {
      v and bit.inv()
    }
  }

  return v
}