//> using scala "3.2.0"
//> using lib "org.typelevel::cats-parse:0.3.7"

import cats.parse.*
import cats.parse.Rfc5234.*
import cats.parse.Parser.*
import scala.collection.mutable.StringBuilder

@main def main = {
  val struct = """Inlined(None, Some(Dupal), Uuua(), Nil, "aaaaaa", Inlined("rec"))"""
  println(AST.parse(struct).map(_.prettyPrint).toOption.get)
}

/*
Inlined(None, Nil, Apply(Select(Ident("asd"), "cos")))

Inlined(
  None,
  Nil,
  Apply(Select(Ident("asd"), "cos"))
)
 */

enum AST {
  case Node(name: String, values: List[AST])
  case Singleton(name: String)
  case Text(value: String)

  def prettyPrint: String = {
    val newline = "\n"
    def ident(using level: Int) = "  " * level
    def append(values: String | Char*)(using acc: StringBuilder) = {
      values.foreach {
        case str: String => acc.append(str)
        case char: Char => acc.append(char)
      }
      acc
    }
    def loop(ast: AST)(using identLevel: Int)(using acc: StringBuilder): StringBuilder =
      ast match {
        case AST.Node(name, values) =>
          append(ident, name, "(")
          values.foreach { ast =>
            append(newline)
            loop(ast)(using identLevel + 1)
            append(",")
          }
          append(newline, ident, ")")

        case AST.Singleton(name) => append(ident, name)
        case AST.Text(value)     => append(ident, '"', value, '"')
      }
    loop(this)(using 0)(using StringBuilder()).result()
  }

  lazy val length: Int =
    this match {
      case AST.Node(name, values) =>
        name.length() + values.map(_.length).sum + 2
      case AST.Singleton(name) =>
        name.length()
      case AST.Text(value) =>
        value.length() + 2
    }
}

object AST {
  private val openParen = string("(").void
  private val closingParen = string(")").void
  private val comma = string(",").void

  private val name = alpha.rep.string

  private val singleton = name.map(AST.Singleton.apply)

  private val text =
    oneOf(alpha :: digit :: charIn(' ', '$', '<', '>') :: Nil).rep0.string.with1
      .surroundedBy(dquote)
      .map(AST.Text.apply)

  private val ast = recursive[AST] { recurse =>
    val container =
      ((name.soft <* openParen) ~ recurse.repSep0(comma <* sp.?.void) <* closingParen)
        .map(AST.Node.apply)

    oneOf(text :: container :: singleton :: Nil)
  }

  def parse(text: String): Either[Error, AST] = ast.parseAll(text)
}
