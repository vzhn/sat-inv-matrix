import funs.add
import variables.IntVar
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberTests {
  @Test
  fun testSettingValue() {
    val v = IntVar(8u)
    
    v.set(3)    
    assertEquals("00000011", v.toString())

    v.set(127)
    assertEquals("01111111", v.toString())
    
    v.set(-3)
    assertEquals("11111101", v.toString())

    v.set(-128)
    assertEquals("10000000", v.toString())
  }
  
  @Test
  fun testAddition() {
    val a = IntVar(8u).also { it.set(2) } 
    val b = IntVar(8u).also { it.set(3) }
    
    assertEquals("00000101", add(a, b).toString())
  }
}