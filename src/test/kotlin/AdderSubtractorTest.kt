import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

data class TestData(val a: Int, val b: Int, val c: Int, val k: Boolean)

class AdderSubtractorTest {
  private val data = listOf(
    TestData(-1, -1, -2, false),
    TestData(3, 2, 5, false),
    TestData(-3, -2, -5, false),
    TestData(-3, 2, -1, false),
    TestData(42, 43, 85, false),
    TestData(3, 2, 1, true),
    TestData(1, 1, 0, true)
  )
  
  @Test
  fun test() {
    val ctx = Context()

    val bitWidth = 8u
    val a = ctx.newVariables(bitWidth)
    val b = ctx.newVariables(bitWidth)
    val k = ctx.newVariable()
    val (sums, c) = ctx.addAdderSubtractor(a, b, k)
    
    for ((ai, bi, ci, ki) in data) {
      ctx.push()

      ctx.assignInt(a, ai)
      ctx.assignInt(b, bi)
      ctx.assign(k, ki)

      val satResult = execZ3(ctx.getCnf())
      assertIs<Z3Result.Sat>(satResult)

      val (assignments) = satResult
      assertEquals(ci, decodeInt(sums, assignments))
      
      ctx.pop()
    }
  }
}

