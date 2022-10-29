import scala.collection.mutable.StringBuilder
import fansi.*

object Printer {
  def print(ast: AST): String = {
    def loop(ast: AST)(using IdentLevel)(using StringBuilder): StringBuilder =
      ast match {
        case AST.Node(name, span, values) =>
          if (span.length >= 40) {
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

        case AST.Singleton(name) => append(ident, Str(name).overlay(Color.Cyan))
        case AST.Text(value)     => append(ident, Str(s""""$value"""").overlay(Color.Green))
      }
    loop(ast)(using IdentLevel.zero)(using StringBuilder()).result()
  }

  private val newline = System.lineSeparator()
  private val openParen = "("
  private val closeParen = ")"

  private def ident(using level: IdentLevel) = "  " * level.value

  private def append(values: String | Char | Str*)(using acc: StringBuilder) = {
    values.foreach {
      case str: String => acc.append(str)
      case char: Char  => acc.append(char)
      case str: Str    => acc.append(str.render)
    }
    acc
  }

  private def appendWhen(cond: Boolean)(values: String | Char | Str*)(using acc: StringBuilder) =
    if (cond) append(values*) else acc
}
