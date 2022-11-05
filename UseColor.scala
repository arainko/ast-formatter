enum UseColor {
  case Yes, No

  def toBoolean: Boolean = 
    this match
      case UseColor.Yes => true
      case UseColor.No => false

  infix def && (that: UseColor): UseColor =
    UseColor.fromBoolean(this.toBoolean && that.toBoolean)
}

object UseColor {
  def fromBoolean(bool: Boolean): UseColor =
    if (bool) UseColor.Yes else UseColor.No
}
