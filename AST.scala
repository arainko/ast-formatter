enum AST {
  case Node(name: String, span: Span, values: Vector[AST])
  case Singleton(name: String)
  case Text(value: String)
  case Number(value: String)

  lazy val length: Int =
    this match {
      case AST.Node(name, span, _) =>
        name.length + span.length + 2
      case AST.Singleton(name) =>
        name.length
      case AST.Text(value) =>
        value.length + 2
      case AST.Number(value) =>
        value.toString.length
    }
}

object AST {
  export Parser.parse
}
