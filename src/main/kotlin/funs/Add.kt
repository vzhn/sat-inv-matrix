package funs

import variables.IntVar

fun add(a: IntVar, b: IntVar): IntVar {
  if (a.width != b.width) throw AssertionError()
  val lhs = a.bits
  val rhs = b.bits
  
  var carry: Boolean = false
  for (i in b.width - 1u downTo 0u) {
    val vleft = a.get(i)
    val vright = b.get(i)
    
  }
  
  val result = IntVar(a.width)
  return result
}