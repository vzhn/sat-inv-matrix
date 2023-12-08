import java.util.regex.Pattern

class SquareMatrix<T>(val n: UInt) {
  val data = mutableMapOf<Pair<UInt, UInt>, T?>()

  fun get(i: UInt, j: UInt): T = data.getValue(i to j)!!
  fun set(i: UInt, j: UInt, v: T) {
    data[i to j] = v
  }



  companion object {
    fun identity(n: UInt): SquareMatrix<Int> {
      val m = SquareMatrix<Int>(n)
      for (row in 0u..<n) {
        for (column in 0u..<n) {
          m.set(row, column, if (row == column) 1 else 0)
        }
      }
      return m
    }

    fun parseIntMatrix(data: String): SquareMatrix<Int> {
      val rows = data.lines().map(String::trim).filterNot(String::isBlank).mapNotNull { line ->
        if (line.isBlank()) null
        else {
          line.split(Pattern.compile("\\s+"))
            .map(String::trim)
            .map(String::toInt)
        }
      }

      val n = rows.size.toUInt()
      val m = SquareMatrix<Int>(n)
      rows.forEachIndexed { rowId, row ->
        row.forEachIndexed { columnId, v ->
          m.set(rowId.toUInt(), columnId.toUInt(), v)
        }
      }

      return m
    }
  }

  fun toString(f: (T) -> String): String {
    return (0u..<n).joinToString(separator = "\n") { row ->
      (0u..<n).joinToString(separator = " ") { column ->  f(get(row, column))}
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as SquareMatrix<*>

    if (n != other.n) return false
    if (data != other.data) return false

    return true
  }

  override fun hashCode(): Int {
    var result = n.hashCode()
    result = 31 * result + data.hashCode()
    return result
  }
}