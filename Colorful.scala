import fansi.*

object Colorful {
  def apply(text: String, color: Attr)(using useColor: UseColor): String =
    useColor match
      case UseColor.Yes => Str(text).overlay(color).render
      case UseColor.No  => text
}
