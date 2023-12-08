import org.apache.commons.cli.*
import java.io.File
import java.io.PrintWriter
import java.util.concurrent.TimeUnit

fun compute(bitWidth: Int, matrixFilePath: String, cnfFilePath: String?) {
  val matrixFile = File(matrixFilePath)
  val cnfFile = cnfFilePath?.let(::File)

  if (!matrixFile.exists()) {
    System.err.println("file '$matrixFilePath' does not exist")
  }

  if (matrixFile.isDirectory) {
    System.err.println("file '$matrixFilePath' is a directory")
  }

  // input matrix
  val ai = SquareMatrix.parseIntMatrix(matrixFile.readText())
  val n = ai.n

  // arithmetic
  val ctx = Context()
  val a = ctx.newMatrix(n, bitWidth.toUInt())
  val b = ctx.newMatrix(n, bitWidth.toUInt())
  val c = ctx.mulMatrix(a, b)

  // assignments
  ctx.assignMatrix(a, ai)
  ctx.assignMatrix(c, SquareMatrix.identity(n))

  // getting CNF
  val cnf = ctx.getCnf()

  println("== input ==")
  println(ai.toString(Int::toString))
  println()

  println("== cnf summary ==")
  println("gates count = ${ctx.gatesCount}")
  println("variables count = ${ctx.variablesCount}")
  println("cnf clauses count = ${cnf.size}")
  println()

  if (cnfFile != null) {
    val pw = PrintWriter(cnfFile)
    pw.print(cnfToDimacs(cnf))
    pw.close()
  }

  val start = System.nanoTime()
  val result = execZ3(cnf)
  val deltaNanos = System.nanoTime() - start
  val timeMillis = TimeUnit.NANOSECONDS.toMillis(deltaNanos)

  println("== z3 time ==")
  println("$timeMillis millis")
  println()

  when (result) {
    is Z3Result.Sat -> {
      val variableAssignments = result.assignments
      val bi = b.decodeMatrix(variableAssignments)
      println("== output ==")
      println(bi.toString(Int::toString))
    }
    Z3Result.Unsat -> System.err.println("Unsatisfiable")
    Z3Result.Error -> System.err.println("failure while calling z3")
  }
}

fun main(args: Array<String>) {
  val options = Options()
  val optionBitWidth = Option.builder("bw")
    .longOpt("bitwidth")
    .required().hasArg()
    .desc("bit width of data vectors (8 for example)")
    .build().also(options::addOption)

  val optionMatrix = Option.builder("m")
    .longOpt("matrix")
    .required().hasArg()
    .desc("Path to text file with matrix")
    .build().also(options::addOption)

  val optionCnf = Option.builder("c")
    .longOpt("cnf")
    .required().hasArg()
    .desc("Path to file for output CNF in DIMACS format")
    .build().also(options::addOption)

  val parser = DefaultParser()
  val helper = HelpFormatter()

  try {
    val cmd = parser.parse(options, args)
    val matrixFile = cmd.getOptionValue(optionMatrix)
    val cnfFile = cmd.getOptionValue(optionCnf)
    val bitWidth = cmd.getOptionValue(optionBitWidth).toIntOrNull()

    if (bitWidth == null) {
      System.err.println("could not parse bit width")
      return
    }

    if (bitWidth < 4) {
      System.err.println("minimal bit width is 4")
      return
    }

    compute(bitWidth, matrixFile, cnfFile)
  } catch (e: ParseException) {
    println(e.message)
    helper.printHelp("arguments", options)
  }
}
