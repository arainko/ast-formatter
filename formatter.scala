//> using scala "3.2.0"
//> using lib "org.typelevel::cats-parse:0.3.7"

import cats.parse.*
import cats.parse.Rfc5234.*
import cats.parse.Parser.*
import scala.collection.mutable

@main def main = {
  val struct = """Inlined(None, Some(Dupal), Nil, "aaaaaa", Inlined("rec"))"""
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
  def prettyPrint: String = {
    val newline = "\n"
    def ident(using level: Int) = "  " * level
    def go(ast: AST)(using identLevel: Int)(using acc: StringBuilder): StringBuilder =
      ast match {
        case AST.Node(name, values) =>
          acc
            .append(ident)
            .append(name)
            .append("(")

            values.foreach { ast => 
              acc.append(newline)
              go(ast)(using identLevel + 1)
              acc.append(", ")
              // acc.append(newline)
            }

            values.mkString

            acc
            .append(newline)
            .append(ident)
            .append(")")
        case AST.Singleton(name) =>
          acc.append(ident).append(name)
        case AST.Text(value)     =>
          acc.append(ident).append('"').append(value).append('"')
      }
    go(this)(using 0)(using mutable.StringBuilder()).result()
  }

  case Node(name: String, values: List[AST])
  case Singleton(name: String)
  case Text(value: String)
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
