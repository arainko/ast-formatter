import scala.collection.mutable.StringBuilder
import fansi.*

opaque type IdentLevel = Int

object IdentLevel {
  val zero: IdentLevel = 0
  def current(using level: IdentLevel): IdentLevel = level
  def apply(level: Int): IdentLevel = level

  extension (self: IdentLevel) {
    def value: Int = self
    infix def + (that: Int): IdentLevel = self + that
  }
}