import kotlin.test.Test
import kotlin.test.assertEquals

class MatrixParserTest {
  private val data = """
    2 1 0 1
    3 2 0 -2
    0 1 1 0
    -1 2 3 4
  """.trimIndent()

  @Test
  fun parseMatrix() {
    val m = SquareMatrix.parseIntMatrix(data)
    assertEquals(2, m.get(0u, 0u))
    assertEquals(1, m.get(0u, 1u))
    assertEquals(0, m.get(0u, 2u))
    assertEquals(1, m.get(0u, 3u))

    assertEquals(3, m.get(1u, 0u))
    assertEquals(2, m.get(1u, 1u))
    assertEquals(0, m.get(1u, 2u))
    assertEquals(-2, m.get(1u, 3u))

    assertEquals(0, m.get(2u, 0u))
    assertEquals(1, m.get(2u, 1u))
    assertEquals(1, m.get(2u, 2u))
    assertEquals(0, m.get(2u, 3u))

    assertEquals(-1, m.get(3u, 0u))
    assertEquals(2, m.get(3u, 1u))
    assertEquals(3, m.get(3u, 2u))
    assertEquals(4, m.get(3u, 3u))
  }
}