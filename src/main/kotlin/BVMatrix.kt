typealias BVMatrix = SquareMatrix<List<Variable>>

fun BVMatrix.decodeMatrix(
  variableAssignments: Map<Variable, Boolean>
): SquareMatrix<Int> {
  val bi = SquareMatrix<Int>(n)
  for (row in 0u..<n) {
    for (column in 0u..<n) {
      bi.set(row, column, decodeInt(get(row, column), variableAssignments))
    }
  }
  return bi
}