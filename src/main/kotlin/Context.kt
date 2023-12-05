class Context {
  private var vcount = 0u
  private val variables = mutableSetOf<Variable>()
  private val _clauses = mutableListOf<MutableList<Gate>>()
  
  val clauses get(): List<Gate> = _clauses.flatten()

  fun addClause(e: Gate) {
    if (_clauses.isEmpty()) {
      _clauses.add(mutableListOf(e))
    } else {
      _clauses.last().add(e)
    }
  }

  fun newVariable(): Variable {
    val v = Variable(1u + (variables.lastOrNull()?.index ?: 0u))
    variables.add(v)
    return v
  }

  fun newVariables(n: UInt): List<Variable> {
    return (1u..n).map { newVariable() }
  }

  private fun add(i1: Variable, i2: Variable, f3: (Variable, Variable, Variable) -> Gate): Variable {
    val o = newVariable()
    addClause(f3(i1, i2, o))
    return o
  }
  
  fun addAnd(i1: Variable, i2: Variable) = add(i1, i2, ::And)

  fun addNand(i1: Variable, i2: Variable) = add(i1, i2, ::Nand)

  fun addOr(i1: Variable, i2: Variable) = add(i1, i2, ::Or)

  fun addNor(i1: Variable, i2: Variable) = add(i1, i2, ::Nor)

  fun addNot(i: Variable): Variable {
    val o = newVariable()
    addClause(Not(i, o))
    return o
  }

  fun addXor(i1: Variable, i2: Variable) = add(i1, i2, ::Xor)

  fun addXnor(i1: Variable, i2: Variable) = add(i1, i2, ::Xnor)
  
  fun addFullAdder(ia: Variable, ib: Variable, ic: Variable): FullAdderOutputs {
    val c1 = addXor(ia, ib)
    val c2 = addXor(c1, ic)    
    val c3 = addAnd(ia, ib)
    val c4 = addAnd(c1, ic)    
    val c5 = addOr(c3, c4)    
    return FullAdderOutputs(c2, c5)
  }
  
  fun addAdderSubtractor(ainputs: List<Variable>, binputs: List<Variable>, k: Variable): AdderSubtractorOutputs {
    if (ainputs.size != binputs.size) throw AssertionError()
    val sums = mutableListOf<Variable>()
    var cin = k
    
    for ((a, b) in ainputs.zip(binputs)) {
      val x = addXor(k, b)
      val (s, c) = addFullAdder(a, x, cin)
      
      sums.add(s)
      cin = c
    }
    
    return AdderSubtractorOutputs(sums, cin)
  }

  /**
   * Tseytin transformation
   * https://en.wikipedia.org/wiki/Tseytin_transformation
   */
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
        is Nand -> {
          val a = cl.i1.index.toInt()
          val b = cl.i2.index.toInt()
          val c = cl.o2.index.toInt()

          result.add(listOf(-a, -b, -c))
          result.add(listOf(a, c))
          result.add(listOf(b, c))
        }
        is Or -> {
          val a = cl.i1.index.toInt()
          val b = cl.i2.index.toInt()
          val c = cl.o2.index.toInt()

          result.add(listOf(a, b, -c))
          result.add(listOf(-a, c))
          result.add(listOf(-b, c))
        }
        is Nor -> {
          val a = cl.i1.index.toInt()
          val b = cl.i2.index.toInt()
          val c = cl.o2.index.toInt()

          result.add(listOf(a, b, c))
          result.add(listOf(-a, -c))
          result.add(listOf(-b, -c))
        }
        is Not -> {
          val a = cl.i.index.toInt()
          val c = cl.o.index.toInt()
          
          result.add(listOf(-a, -c))
          result.add(listOf(a, c))
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
        is Xnor -> {
          val a = cl.i1.index.toInt()
          val b = cl.i2.index.toInt()
          val c = cl.o2.index.toInt()

          result.add(listOf(-a, -b, c))
          result.add(listOf(a, b, c))
          result.add(listOf(a, -b, -c))
          result.add(listOf(-a, b, -c))
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

  fun assignInt(a: List<Variable>, v: Int) {
    val uv: UInt = (if (v >= 0) v else (-(v + 1))).toUInt()
    val isNegative = v < 0

    val bits = mutableListOf<Boolean>()
    for (b in 0..<a.size - 1) {
      bits.add(((uv and (1u shl b)) != 0u) != isNegative)
    }
    bits.add(isNegative)
    
    for ((lit, value) in a.zip(bits)) {
      assign(lit, value)
    }
  }
}

data class FullAdderOutputs(val s: Variable, val c: Variable)
data class AdderSubtractorOutputs(val sums: List<Variable>, val c: Variable)