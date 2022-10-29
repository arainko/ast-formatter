import cats.parse.*
import cats.parse.Rfc5234.*
import cats.parse.Parser.*

object Parser {
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
      (
        (name.soft <* openParen) ~
          index ~
          recurse.repSep0(comma <* sp.?.void) ~
          index <* closingParen
      ).map {
        case name -> start -> asts -> end =>
          AST.Node(name, Span(start, end), asts.toVector)
      }

    oneOf(text :: container :: singleton :: Nil)
  }

  def parse(text: String): Either[Error, AST] = ast.parseAll(text)
}



