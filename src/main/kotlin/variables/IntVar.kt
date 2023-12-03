package variables

import kotlin.math.absoluteValue

class IntVar(val width: UInt) {
  val bits = mutableMapOf<UInt, Boolean>()
  
  init {
    reset()
  }
  
  fun get(index: UInt): Boolean {
    return bits[index]!!
  }
  
  fun set(v: Int) {
    reset()

    if (v < 0) {
      val absoluteValue = (-v - 1).absoluteValue.toUInt()
      setPositive(absoluteValue)
      invert()
    } else {
      setPositive(v.absoluteValue.toUInt())
    }
  }

  private fun setPositive(absoluteValue: UInt) {
    for (i in width - 1u downTo 0u) {
      bits[i] = (absoluteValue.shr(i.toInt()).and(1u) == 1u)
    }
  }

  fun invert() {
    for (i in 0u..<width) {
      bits[i] = bits[i]!!.not()
    }
  }

  private fun reset() {
    for (i in 0u..<width) {
      bits[i] = false
    }
  }

  override fun toString(): String {
    val chars = mutableListOf<Char>()
    for (i in width - 1u downTo 0u) {
      chars.add(if (bits[i]!!) '1' else '0')
    }
    return chars.joinToString("")
  }
}