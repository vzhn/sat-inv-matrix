import java.util.concurrent.TimeUnit

/**
 * Generates a pair of matrix which product is equal identity matrix
 */
fun main() {
  val n = 5u
  val bitWidth = 8

  val ctx = Context()
  val a = ctx.newMatrix(n, bitWidth.toUInt())
  val b = ctx.newMatrix(n, bitWidth.toUInt())
  val c = ctx.mulMatrix(a, b)

  ctx.assignMatrix(c, SquareMatrix.identity(n))

  val cnf = ctx.getCnf()
  val start = System.nanoTime()
  val result = execSolver(Solver.Z3, cnf)
  val millis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
  println("time: $millis millis")

  when (result) {
    is SATResult.Sat -> {
      val ai = a.decodeMatrix(result.assignments)
      val bi = b.decodeMatrix(result.assignments)
      println("== A ==")
      println(ai.toString(Int::toString))
      println()

      println("== B ==")
      println(bi.toString(Int::toString))
    }
    SATResult.Error -> System.err.println("solver error")
    SATResult.Unsat -> System.err.println("unsat")
  }
}