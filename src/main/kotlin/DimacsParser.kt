import java.text.ParseException
import java.util.regex.Pattern

class DimacsParser {
  var sat: Boolean? = null
  val assignments = mutableSetOf<Int>()

  fun parseLines(text: String) {
    text.lines()
      .map(String::trim)
      .filterNot(String::isBlank)
      .forEach(::parse)
  }

  private fun parse(line: String) {
    when (line.first()) {
      'c' -> Unit
      's' -> sat = when (line) {
        "s SATISFIABLE" -> true
        "s UNSATISFIABLE" -> false
        else -> throw ParseException("could not parse line: $line", 0)
      }
      'v' -> {
        line.substring(2)
          .split(Pattern.compile("\\s+"))
          .filterNot(String::isBlank)
          .map(String::toInt)
          .filterNot { it == 0 }
          .toCollection(assignments)
      }
      else -> throw ParseException("could not parse line: $line", 0)
    }
  }
}