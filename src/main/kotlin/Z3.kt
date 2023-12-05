import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import kotlin.math.absoluteValue

sealed class Z3Result {
  data object Unsat: Z3Result()
  data object Error: Z3Result()
  data class Sat(
    val assignments: Map<UInt, Boolean>
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
    val header = inputStream.readLine()
    
    return if (header.equals("s SATISFIABLE")) {
      val body = inputStream.readLine()
      val parts = body.split(" ")
      val assignments = mutableMapOf<UInt, Boolean>()
      for (v in parts.subList(1, parts.size).filterNot(String::isBlank).map(String::toInt)) {
        assignments[v.absoluteValue.toUInt()] = v > 0
      }
      Z3Result.Sat(assignments)
    } else if (header.equals("s UNSATISFIABLE")) {
      Z3Result.Unsat
    } else {
      Z3Result.Error
    }
  } finally {
    process.inputStream.close()
    process.outputStream.close()
    process.errorStream.close()
  }  
}