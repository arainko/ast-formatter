final case class Span(start: Int, end: Int) {
  val length: Int = end - start
}