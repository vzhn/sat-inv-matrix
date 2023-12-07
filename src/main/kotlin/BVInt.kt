
class BVInt(val bits: List<Boolean>) {
  val isPositive get() = !bits.last()
  val width get() = bits.size

  fun toInt(): Int {
    var v = if (isPositive) 0 else -1

    for (i in 0..<width - 1) {
      val bit = bits[i]
      v = if (bit) {
        v or (1 shl i)
      } else {
        v and (1 shl i).inv()
      }
    }
    return v
  }

  companion object {
    fun fromInt(v: Int, width: Int): BVInt {
      if (width < 2) throw AssertionError("width < 2")

      val isPositive = v >= 0
      val data = mutableListOf<Boolean>()
      (1..width).forEach { _ -> data.add(false) }

      var lv = v.toLong()
      if (!isPositive) {
        lv = -lv - 1
      }

      for (i in 0..<width - 1) {
        data[i] = lv and (1.toLong() shl i) != 0.toLong()
      }

      if (!isPositive) {
        data.indices.forEach { ix -> data[ix] = data[ix].not() }
      }

      return BVInt(data)
    }
  }

  override fun toString(): String {
    return bits.asReversed().joinToString("") { if (it) "1" else "0" }
  }
}