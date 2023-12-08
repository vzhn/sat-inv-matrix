import kotlin.test.Test
import kotlin.test.assertEquals

class DimacsCNFParserTest {
  private val dataSat = """
    c Total time (this thread) : 0.02        
    s SATISFIABLE
    v -1 2 -3 
    v -13 -14 
  """.trimIndent()

  private val dataUnsat = """
    c Total time (this thread) : 0.02        
    s UNSATISFIABLE 
  """.trimIndent()

  @Test
  fun testSat() {
    val p = DimacsParser()
    p.parseLines(dataSat)

    assertEquals(true, p.sat)
    assertEquals(setOf(-1, 2, -3, -13, -14), p.assignments)
  }

  @Test
  fun testUnsat() {
    val p = DimacsParser()
    p.parseLines(dataUnsat)

    assertEquals(false, p.sat)
  }
}

