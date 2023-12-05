fun cnfToDimacs(cnf: List<List<Int>>): String {
  val nvars = cnf.flatten().toSet().count()
  val nclauses = cnf.size
  
  val header = "p cnf $nvars $nclauses"
  val clauses = cnf.joinToString(separator = "\n") { clause -> clause.joinToString(separator = " ") + " 0" }
  return header + "\n" + clauses
}