import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import kotlin.math.absoluteValue

sealed class Z3Result {
  data object Unsat: Z3Result()
  data object Error: Z3Result()
  data class Sat(
    val assignments: Map<Variable, Boolean>
  ): Z3Result()
}

fun execZ3(cnf: List<List<Int>>): Z3Result {
  val r = Runtime.getRuntime()
  val process = r.exec("z3 -in -dimacs")  
  try {
    val os = PrintStream(process.outputStream)
    os.println(cnfToDimacs(cnf))
    os.close()

    val inputStream = BufferedReader(InputStreamReader(process.inputStream))
    val parser = DimacsParser()
    parser.parseLines(inputStream.readText())

    if (parser.sat ?: return Z3Result.Error) {
      val assignments = mutableMapOf<Variable, Boolean>()
      for (iv in parser.assignments) {
        assignments[Variable(iv.absoluteValue.toUInt())] = iv > 0
      }
      return Z3Result.Sat(assignments)
    } else {
      return Z3Result.Unsat
    }
  } finally {
    process.inputStream.close()
    process.outputStream.close()
    process.errorStream.close()
  }  
}
