import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

data class FullAdderData(
  val a: Boolean, val b: Boolean, val cin: Boolean,
  val s: Boolean, val cout: Boolean
)

class FullAdderTest {  
  private val data = listOf(
    FullAdderData(a = false, b = false, cin = false, s = false, cout = false),
    FullAdderData(a = false, b = false, cin = true, s = true, cout = false),
    FullAdderData(a = false, b = true, cin = false, s = true, cout = false),
    FullAdderData(a = false, b = true, cin = true, s = false, cout = true),
    FullAdderData(a = true, b = false, cin = false, s = true, cout = false),
    FullAdderData(a = true, b = false, cin = true, s = false, cout = true),
    FullAdderData(a = true, b = true, cin = false, s = false, cout = true),
    FullAdderData(a = true, b = true, cin = true, s = true, cout = true),    
  )
  
  @Test
  fun testFullAdder() {
    val ctx = Context()
    val (av, bv, cinv) = ctx.newVariables(3u)
    val (sv, coutv) = ctx.addFullAdder(av, bv, cinv)
    
    for ((a, b, cin, s, cout) in data) {
      ctx.push()
      
      ctx.assign(av, a)
      ctx.assign(bv, b)
      ctx.assign(cinv, cin)
      
      val result = execZ3(ctx.getCnf())      
      assertIs<Z3Result.Sat>(result)
      assertEquals(s, result.assignments.getValue(sv.index))
      assertEquals(cout, result.assignments.getValue(coutv.index))
      
      ctx.pop()
    }
  }
}