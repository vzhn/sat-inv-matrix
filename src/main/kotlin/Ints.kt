
fun decodeInt(a: List<Variable>, assignments: Map<Variable, Boolean>): Int {
  return BVInt(a.map { assignments.getValue(it) }).toInt()
}