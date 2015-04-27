package theoremProver

import scala.language.implicitConversions

class Sequent {
  var premises: Map[Expression, Int] = _
  var conclusions: Map[Expression, Int] = _
  var depth: Int = _

  def isOverlap: Boolean =
    premises.keys exists(x => conclusions.keys exists(y => x == y))

  override def toString =
    premises.keys.mkString(", ") + " => " + conclusions.keys.mkString(", ")
}

object Prover {
  implicit def expToBool(exp: Expression): Boolean = NoneExpression != exp

  var premises: Set[Sequent] = Set()
  var conclusion: Array[Sequent] = Array()

  def prove(pres: Array[Expression], con: Expression): Boolean = {
    val seq = new Sequent {
      premises = (pres map(f => (f, 0))).toMap
      conclusions = Map(con -> 0)
      depth = 0
    }
    premises = Set(seq)
    conclusion = Array(seq)

    scan()
  }

  def scan(): Boolean = {
    var curSeq: Sequent = new Sequent

    var break = false
    while(! conclusion.isEmpty && (! break || (premises contains curSeq))) {
      curSeq = conclusion(0)
      conclusion = conclusion drop 1
      break = true
    }
    if( ! break) true
    else {
      printSequent(curSeq)
      if(curSeq.isOverlap) {
        premises += curSeq
        scan()
      }
      else process(curSeq)
    }
  }

  def process(seq: Sequent): Boolean = {

    var applyPre = false

    var preDepth: Int = 0
    var pre: Expression = NoneExpression
    for((exp, depth) <- seq.premises)
      if((preDepth == 0 || preDepth > depth) && ! exp.isInstanceOf[Atom]) {
        pre = exp
        preDepth = depth
        applyPre = true
      }
    var conDepth: Int = 0
    var con: Expression = NoneExpression
    for((exp, depth) <- seq.conclusions)
      if((conDepth == 0 || conDepth > depth) && ! exp.isInstanceOf[Atom]) {
        con = exp
        conDepth = depth
        applyPre = false
      }
    if(! (pre || con)) false
    else {
      val seqA = new Sequent {
        premises = seq.premises
        conclusions = seq.conclusions
        depth = seq.depth + 1
      }
      val seqB = new Sequent {
        premises = seq.premises
        conclusions = seq.conclusions
        depth = seq.depth + 1
      }

      applyPre match {
        case true => {
          seqA.premises -= pre
          seqB.premises -= pre

          val count = seq.premises(pre) + 1

          pre match {
            case x: Not => {
              seqA.conclusions = seqA.conclusions updated(x.expression, count)
              conclusion :+= seqA
              scan()
            }
            case x: And => {
              seqA.premises = seqA.premises updated(x.lExpr, count)
              seqA.premises = seqA.premises updated(x.rExpr, count)
              conclusion :+= seqA
              scan()
            }
            case x: Or => {
              seqA.premises = seqA.premises updated(x.lExpr, count)
              seqB.premises = seqB.premises updated(x.rExpr, count)
              conclusion :+= seqA
              conclusion :+= seqB
              scan()
            }
            case x: Implies => {
              val temp = new Not(x.lExpr)
              seqA.premises = seqA.premises updated(temp, count)
              seqB.premises = seqA.premises updated(x.rExpr, count)
              conclusion :+= seqA
              conclusion :+= seqB
              scan()
            }
            case x: Equiv => {
              val tempA = new Implies(x.lExpr, x.rExpr)
              val tempB = new Implies(x.rExpr, x.lExpr)
              seqA.premises = seqA.premises updated(tempA, count)
              seqA.premises = seqA.premises updated(tempB, count)
              conclusion :+= seqA
              scan()
            }
            case _ => process(seq)
          }
        }
        case false => {
          seqA.conclusions -= con
          seqB.conclusions -= con

          val count = seq.conclusions(con) + 1

          con match {
            case x: Not => {
              seqA.premises = seqA.premises updated(x.expression, count)
              conclusion :+= seqA
              scan()
            }
            case x: And => {
              seqA.conclusions = seqA.conclusions updated(x.lExpr, count)
              seqB.conclusions = seqB.conclusions updated(x.rExpr, count)
              conclusion :+= seqA
              conclusion :+= seqB
              scan()
            }
            case x: Or => {
              seqA.conclusions = seqA.conclusions updated(x.lExpr, count)
              seqA.conclusions = seqA.conclusions updated(x.rExpr, count)
              conclusion :+= seqA
              scan()
            }
            case x: Implies => {
              seqA.premises = seqA.premises updated(x.lExpr, count)
              seqA.conclusions = seqA.conclusions updated(x.rExpr, count)
              conclusion :+= seqA
              scan()
            }
            case x: Equiv => {
              val tempA = new Implies(x.lExpr, x.rExpr)
              val tempB = new Implies(x.rExpr, x.lExpr)
              seqA.conclusions = seqA.conclusions updated(tempA, count)
              seqA.conclusions = seqA.conclusions updated(tempB, count)
              conclusion :+= seqA
              scan()
            }
            case _ => process(seq)
          }
        }
      }
    }
  }

  def printSequent(seq: Sequent) {
    println("[" + seq.depth + "]" + " " + seq)
  }
}
