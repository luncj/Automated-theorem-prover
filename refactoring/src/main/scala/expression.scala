package theoremProver

sealed abstract class Expression {
  // override def equals(that: Any): Boolean
  // def ==(that: Any): Boolean
  // def !=(that: Any): Boolean = ! ==(that)
  def log(msg: String): String = "(" + msg + ")"
}

object NoneExpression extends Expression {
  // def ==(that: Any) = this equals that
  // override def equals(that: Any): Boolean = that.[NoneExpression]
  override def toString = "None Expression"
}

class AtomExpression(token: String) extends Expression {
  val expression = token

  override def equals(that: Any): Boolean = that match {
    case x: AtomExpression => x.expression == expression
    case _ => false
  }

  // def ==(that: Any) = that match {
  //   case x: AtomExpression =>
  //     x.expression == expression
  //   case _ => false
  // }

  override def toString = expression
}

class NotExpression(token: Expression) extends Expression {
  val expression = token

  override def equals(that: Any): Boolean = that match {
    case x: NotExpression => x.expression == expression
    case _ => false
  }

  // def ==(that: Any) = that match {
  //   case x: NotExpression =>
  //     x.expression == expression
  //   case _ => false
  // }

  override def toString = log("¬ " + expression)
}

sealed abstract class BinaryExpression(lExp: Expression, rExp: Expression) extends Expression{
  val left = lExp
  val right = rExp

  val operator: String

  override def toString = log(left + " " + operator + " " + right)
}

abstract class BrotherExpression(lExp: Expression, rExp: Expression) extends BinaryExpression(lExp, rExp) {
  def brother[T <: Expression](child: T) =
    if(child == left) right
    else if(child == right) left
    else NoneExpression
}

class AndExpression(lExp: Expression, rExp: Expression)
    extends BrotherExpression(lExp, rExp) {

  val operator = "∧"

  override def equals(that: Any) = that match {
    case x: AndExpression =>
      (x.left == left && x.right == right) || (x.left == right && x.right == left)
    case _ => false
  }

  // def ==(that: Any) = that match {
  //   case x: AndExpression =>
  //     (x.left == left && x.right == right) || (x.left == right && x.right == left)
  //   case _ => false
  // }
}

class OrExpression(lExp: Expression, rExp: Expression)
    extends BrotherExpression(lExp, rExp) {

  val operator = "∨"

  override def equals(that: Any) = that match {
    case x: OrExpression =>
      (x.left == left && x.right == right) || (x.left == right && x.right == left)
    case _ => false
  }

  // def ==(that: Any) = that match {
  //   case x: OrExpression =>
  //     (x.left == left && x.right == right) || (x.left == right && x.right == left)
  //   case _ => false
  // }
}

class ImpExpression(lExp: Expression, rExp: Expression)
    extends BinaryExpression(lExp, rExp) {

  val operator = "→"

  override def equals(that: Any) = that match {
    case x: ImpExpression =>
      x.left == left && x.right == right
    case _ => false
  }

  // def ==(that: Any) = that match {
    // case x: ImpExpression =>
      // x.left == left && x.right == right
    // case _ => false
  // }
}

class EquiExpression(lExp: Expression, rExp: Expression)
    extends BrotherExpression(lExp, rExp) {

  val operator = "↔"

  override def equals(that: Any) = that match {
    case x: EquiExpression =>
      (x.left == left && x.right == right) || (x.left == right && x.right == left)
    case _ => false
  }


  // def ==(that: Any) = that match {
  //   case x: EquiExpression =>
  //     (x.left == left && x.right == right) || (x.left == right && x.right == left)
  //   case _ => false
  // }
}
