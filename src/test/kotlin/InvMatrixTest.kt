import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class InvMatrixTest {
  private val data = """
    2 1 0 1
    3 2 0 -2
    0 1 1 0
    -1 2 3 4
  """.trimIndent()

  private val invData = """
    6 -5 12 -4
    -10 9 -21 7
    10 -9 22 -7
    -1 1 -3 1
  """.trimIndent()

  @Test
  fun test() {
    val bitWidth = 8u

    val ctx = Context()
    val ai = SquareMatrix.parseIntMatrix(data)
    val n = ai.n

    val a = ctx.newMatrix(n, bitWidth)
    val b = ctx.newMatrix(n, bitWidth)
    val c = ctx.mulMatrix(a, b)

    ctx.assignMatrix(a, ai)
    ctx.assignMatrix(c, SquareMatrix.identity(n))

    val result = execZ3(ctx.getCnf())
    assertIs<Z3Result.Sat>(result)

    val variableAssignments = result.assignments.toVariableAssignments()
    val bi = SquareMatrix<Int>(n)
    for (row in 0u..<n) {
      for (column in 0u..<n) {
        val v = decodeInt(b.get(row, column), variableAssignments)
        bi.set(row, column, v)
      }
    }

    assertEquals(SquareMatrix.parseIntMatrix(invData), bi)
  }
}