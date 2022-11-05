import fansi.*

object Colorful {
  def apply(text: String, color: Attr)(using useColor: UseColor): String =
    useColor match {
      case UseColor.Yes => Str(text).overlay(color).render
      case UseColor.No  => text
    }

  val Hue = 
    Vector(
      Color.True(204, 51, 51),

      Color.True(204, 89, 51),
      Color.True(204, 128, 51),
      Color.True(204, 166, 51),  
      Color.True(204, 204, 51),

      Color.True(166, 204, 51),
      Color.True(128, 204, 51),
      Color.True(89, 204, 51),
      Color.True(51, 204, 51),

      Color.True(51, 204, 89),
      Color.True(51, 204, 128),
      Color.True(51, 204, 166),
      Color.True(51, 204, 204),

      Color.True(51, 166, 204),
      Color.True(51, 128, 204),
      Color.True(51, 89, 204),
      Color.True(51, 51, 204),

      Color.True(89, 51, 204),
      Color.True(128, 51, 204),
      Color.True(166, 51, 204),
      Color.True(204, 51, 204),

      Color.True(204, 51, 166),
      Color.True(204, 51, 128),
      Color.True(204, 51, 89),
    )
}
