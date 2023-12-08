import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import kotlin.math.absoluteValue

sealed class SATResult {
  data object Unsat: SATResult()
  data object Error: SATResult()
  data class Sat(val assignments: Map<Variable, Boolean>): SATResult()
}

enum class Solver(val command: String) {
  Z3("z3 -in -dimacs"),
  CRYPTOMINISAT("cryptominisat5 --verb 0")
}

fun execSolver(s: Solver, cnf: List<List<Int>>): SATResult {
  val r = Runtime.getRuntime()
  val process = r.exec(s.command)
  // val process = r.exec("cryptominisat5 --verb 0")
  try {
    val os = PrintStream(process.outputStream)
    os.println(cnfToDimacs(cnf))
    os.close()

    val inputStream = BufferedReader(InputStreamReader(process.inputStream))
    val parser = DimacsParser()
    parser.parseLines(inputStream.readText())

    if (parser.sat ?: return SATResult.Error) {
      val assignments = mutableMapOf<Variable, Boolean>()
      for (iv in parser.assignments) {
        assignments[Variable(iv.absoluteValue.toUInt())] = iv > 0
      }
      return SATResult.Sat(assignments)
    } else {
      return SATResult.Unsat
    }
  } finally {
    process.inputStream.close()
    process.outputStream.close()
    process.errorStream.close()
  }  
}
