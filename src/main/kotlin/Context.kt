class Context {
  private var vcount = 0u
  private val variables = mutableSetOf<Variable>()
  private val _clauses = mutableListOf<MutableList<Expression>>()
  
  val clauses get(): List<Expression> = _clauses.flatten()
  
  fun addClause(e: Expression) {
    if (_clauses.isEmpty()) {
      _clauses.add(mutableListOf(e))
    } else {
      _clauses.last().add(e)
    }
  }
  
  fun newVariable(): Variable {
    val v = Variable(++vcount)
    variables.add(v)
    return v
  }
  
  fun newVariables(n: UInt): List<Variable> {
    return (1u..n).map { newVariable() }
  }
  
  fun addNot(i: Variable): Variable {
    val o = newVariable()
    addClause(Not(i, o))
    return o
  }
  
  fun addXor(i1: Variable, i2: Variable): Variable {
    val o = newVariable()
    addClause(Xor(i1, i2, o))
    return o
  }

  fun addOr(i1: Variable, i2: Variable): Variable {
    val o = newVariable()
    addClause(Or(i1, i2, o))
    return o
  }

  fun addAnd(i1: Variable, i2: Variable): Variable {
    val o = newVariable()
    addClause(And(i1, i2, o))
    return o
  }
  
  fun addFullAdder(ia: Variable, ib: Variable, ic: Variable): List<Variable> {
    val c1 = addXor(ia, ib)
    val c2 = addXor(c1, ic)    
    val c3 = addAnd(ia, ib)
    val c4 = addAnd(c1, ic)    
    val c5 = addOr(c3, c4)    
    return listOf(c2, c5)
  }
  
  fun getCnf(): MutableList<List<Int>> {
    val result = mutableListOf<List<Int>>()
    for (cl in clauses) {
      when (cl) {
        is And -> {
          val a = cl.i1.index.toInt()
          val b = cl.i2.index.toInt()
          val c = cl.o2.index.toInt()
          
          result.add(listOf(-a, -b, c))
          result.add(listOf(a, -c))
          result.add(listOf(b, -c))
        }
        is Not -> {
          val a = cl.i.index.toInt()
          val c = cl.o.index.toInt()
          
          result.add(listOf(-a, -c))
          result.add(listOf(a, c))
        }
        is Or -> {
          val a = cl.i1.index.toInt()
          val b = cl.i2.index.toInt()
          val c = cl.o2.index.toInt()
          
          result.add(listOf(a, b, -c))
          result.add(listOf(-a, c))
          result.add(listOf(-b, c))
        }
        is Xor -> {
          val a = cl.i1.index.toInt()
          val b = cl.i2.index.toInt()
          val c = cl.o2.index.toInt()

          result.add(listOf(-a, -b, -c))
          result.add(listOf(a, b, -c))
          result.add(listOf(a, -b, c))
          result.add(listOf(-a, b, c))
        }
        is Literal -> {
          val a = cl.v.index.toInt()
          result.add(listOf(if (cl.isPositive) a else -a))
        }
      }
    }
    
    return result
  }

  fun assign(av: Variable, isPositive: Boolean) {
    addClause(Literal(av, isPositive))
  }

  fun push() {
    _clauses.add(mutableListOf())
  }
  
  fun pop() {
    _clauses.removeLast()
  }
}