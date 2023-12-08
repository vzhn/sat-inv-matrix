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
    
    val result = execZ3(ctx.getCnf())
    assertIs<Z3Result.Sat>(result)

    assertEquals(126, decodeInt(c, result.assignments))
  }
}