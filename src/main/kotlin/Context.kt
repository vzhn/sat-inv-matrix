class Context {
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

  fun newMatrix(n: UInt, bitWidth: UInt): BVMatrix {
    val m = SquareMatrix<List<Variable>>(n)
    for (rowIndex in 0u..<n) {
      for (columnIndex in 0u..<n) {
        m.set(rowIndex, columnIndex, newVariables(bitWidth))
      }
    }
    return m
  }

  fun mulMatrix(a: BVMatrix, b: BVMatrix): BVMatrix {
    if (a.n != b.n ) throw AssertionError("matrix dimensions does not match")
    val n = a.n

    val falseConst = newVariable()
    assign(falseConst, false)

    val c = BVMatrix(n)

    for (row in 0u..<n) {
      for (col in 0u..<n) {
        var sum: List<Variable>? = null

        for (k in 0u..<n) {
          val av = a.get(row, k)
          val bv = b.get(k, col)
          val (cv) = addMultiplier(av, bv)

          if (sum == null) {
            sum = cv
          } else {
            val (s) = addAdderSubtractor(sum, cv, falseConst)
            sum = s
          }
        }
        c.set(row, col, sum!!)
      }
    }

    return c
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
  
  fun addHalfAdder(ia: Variable, ib: Variable): HalfAdderOutputs {
    val c1 = addXnor(ia, ib)
    val c2 = addAnd(ia, ib)
    return HalfAdderOutputs(c1, c2)
  }

  fun addBaughWooleyCell(si: Variable, ci: Variable, a: Variable, b: Variable, type: BaughWooleyType): FullAdderOutputs {
    val g = (if (type == BaughWooleyType.WHITE) ::addAnd else ::addNand)(a, b)
    return addFullAdder(g, si, ci)
  }

  /*
  * Baugh-Wooley multiplier
  */
  fun addMultiplier(ainputs: List<Variable>, binputs: List<Variable>): MultiplicationOutputs {
    if (ainputs.size != binputs.size) throw AssertionError()

    val falseConst = newVariable()
    assign(falseConst, false)

    val trueConst = newVariable()
    assign(trueConst, true)
    
    val ands = mutableMapOf<Pair<Int, Int>, Variable>()
    val fas = mutableMapOf<Pair<Int, Int>, FullAdderOutputs>()

    for (bi in binputs.indices) {
      for (ai in ainputs.indices) {
        val isFirstRow = bi == 0
        val isSecondRow = bi == 1

        val isLastCol = ai == ainputs.lastIndex
        val isPreLastCol = ai == ainputs.lastIndex - 1

        val isLastRow = bi == binputs.lastIndex
        val a = ainputs[ai]
        val b = binputs[bi]
        val pos = bi to ai

        ands[pos] = when {
          isLastRow && !isLastCol -> addAnd(addNot(a), b)
          isLastCol && !isLastRow -> addAnd(a, addNot(b))
          else -> addAnd(a, b)
        }

        if (!isFirstRow && !isLastCol) {
          val leftTopPos = bi - 1 to ai + 1
          val leftTop = if (isPreLastCol || isSecondRow) {
            ands.getValue(leftTopPos)
          } else {
            fas.getValue(leftTopPos).s
          }

          val carry = if (isSecondRow) {
            falseConst
          } else {
            fas.getValue(bi - 1 to ai).c
          }

          fas[pos] = addFullAdder(leftTop, carry, ands.getValue(bi to ai))
        }
      }
    }

    val leftBottom = binputs.lastIndex to ainputs.lastIndex
    fas[leftBottom] = addFullAdder(addNot(binputs.last()), addNot(ainputs.last()), ands.getValue(leftBottom))

    val f4x = addFullAdder(fas.getValue(binputs.lastIndex to 0).s, ainputs.last(), binputs.last())
    var c = f4x.c
    
    for (i in ainputs.indices) {
      val leftTop = if (i != ainputs.lastIndex) {
        fas.getValue(binputs.lastIndex to i + 1).s
      } else {
        trueConst
      }
      
      val top = fas.getValue(binputs.lastIndex to i).c
      val fa = addFullAdder(leftTop, top, c)
      fas[binputs.lastIndex + 1 to i] = fa
      c = fa.c
    }

    val muls = mutableListOf<Variable>()
    muls.add(ands.getValue(0 to 0))
    
    for (i in 1..<binputs.lastIndex) {
      muls.add(fas.getValue(i to 0).s)
    }
    
    muls.add(f4x.s)
    
    for (i in 0..<ainputs.lastIndex) {
      muls.add(fas.getValue(binputs.lastIndex + 1 to i).s)
    }
    
    return MultiplicationOutputs(muls, c)
  }
  
  fun addAdderSubtractor(ainputs: List<Variable>, binputs: List<Variable>, k: Variable): AdderSubtractorOutputs {
    if (ainputs.size != binputs.size) throw AssertionError()
    val sums = mutableListOf<Variable>()
    var cin = k
    
    for ((a, b) in ainputs.zip(binputs)) {
      val (s, c) = addFullAdder(a, addXor(k, b), cin)
      
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
    for ((variable, value) in a.zip(BVInt.fromInt(v, a.size).bits)) {
      assign(variable, value)
    }
  }

  fun assignMatrix(m: BVMatrix, im: SquareMatrix<Int>) {
    if (m.n != im.n) throw AssertionError("matrix dimensions does not match")
    val n = m.n

    for (row in 0u..<n) {
      for (column in 0u..<n) {
        assignInt(m.get(row, column), im.get(row, column))
      }
    }
  }
}

data class FullAdderOutputs(val s: Variable, val c: Variable)
data class HalfAdderOutputs(val s: Variable, val c: Variable)
data class AdderSubtractorOutputs(val sums: List<Variable>, val c: Variable)
data class MultiplicationOutputs(val a: List<Variable>, val c: Variable)
enum class BaughWooleyType { WHITE, GRAY }