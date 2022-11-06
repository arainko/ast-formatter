trait IntNewtype {
  opaque type Type = Int

  val zero: Type = 0
  def current(using level: Type): Type = level
  def apply(level: Int): Type = level

  extension (self: Type) {
    def value: Int = self
    infix def + (that: Int): Type = self + that
  }
}

type IdentLevel = IdentLevel.Type
object IdentLevel extends IntNewtype

type Depth = Depth.Type
object Depth extends IntNewtype