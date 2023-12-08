import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MultiplierTest {
  @Test
  fun test() {
    val ctx = Context()
    val bitWidth = 8u
    val a = ctx.newVariables(bitWidth)
    val b = ctx.newVariables(bitWidth)
    
    ctx.assignInt(a, 3)
    ctx.assignInt(b, 42)
    val (c) = ctx.addMultiplier(a, b)
    
    val result = execSolver(Solver.Z3, ctx.getCnf())
    assertIs<SATResult.Sat>(result)

    assertEquals(126, decodeInt(c, result.assignments))
  }
}