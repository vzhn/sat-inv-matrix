import kotlin.test.Test
import kotlin.test.assertEquals

class BVIntTests {
  @Test
  fun testFromInt() {
    assertEquals("11111110", BVInt.fromInt(-2, 8).toString())
    assertEquals("11111111", BVInt.fromInt(-1, 8).toString())
    assertEquals("00000100", BVInt.fromInt(4, 8).toString())
    assertEquals("00000101", BVInt.fromInt(5, 8).toString())
    assertEquals("00101010", BVInt.fromInt(42, 8).toString())
    assertEquals("11010110", BVInt.fromInt(-42, 8).toString())
  }

  @Test
  fun testToInt() {
    for (i in listOf(42, -42, -1, 2, 0)) {
      assertEquals(i, BVInt.fromInt(i, 8).toInt())
    }
  }
}