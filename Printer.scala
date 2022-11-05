import scala.collection.mutable.StringBuilder
import fansi.*

object Printer {
  def print(ast: AST, useColor: UseColor): String = {
    def loop(ast: AST)(using IdentLevel)(using StringBuilder, UseColor): StringBuilder =
      ast match {
        case AST.Node(name, span, values) =>
          if (span.length >= 80) {
            append(ident, name, openParen)
            values.zipWithIndex.foreach { (ast, idx) =>
              append(newline)
              loop(ast)(using IdentLevel.current + 1)
              appendWhen(idx < values.size - 1)(", ")
            }
            append(newline, ident, closeParen)
          } else {
            append(ident, name, openParen)
            values.zipWithIndex.foreach { (ast, idx) =>
              loop(ast)(using IdentLevel.zero)
              appendWhen(idx < values.size - 1)(", ")
            }
            append(closeParen)
          }

        case AST.Singleton(name) => append(ident, Colorful(name, Color.Cyan))
        case AST.Text(value)     => append(ident, Colorful(s""""$value"""", Color.Green))
        case AST.Number(value)   => append(ident, Colorful(value, Color.LightMagenta))
      }
    loop(ast)(using IdentLevel.zero)(using StringBuilder(), useColor).result()
  }

  private val newline = System.lineSeparator()
  private val openParen = "("
  private val closeParen = ")"

  private def ident(using level: IdentLevel) = "  " * level.value

  private def append(values: String | Char*)(using acc: StringBuilder) = {
    values.foreach {
      case str: String => acc.append(str)
      case char: Char  => acc.append(char)
    }
    acc
  }

  private def appendWhen(cond: Boolean)(values: String | Char*)(using acc: StringBuilder) =
    if (cond) append(values*) else acc
}
