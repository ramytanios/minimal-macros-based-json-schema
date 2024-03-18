package schema

import scala.annotation.StaticAnnotation

package annotations {

  case class GreatherThan(lb: Double) extends StaticAnnotation

  object GreatherThan {
    implicit val repr: Repr[GreatherThan] = Repr.factory(a => List(("minimum", a.lb)))
  }

  case class LessThan(ub: Double) extends StaticAnnotation

  object LessThan {
    implicit val repr: Repr[LessThan] = Repr.factory(a => List(("maximum", a.ub)))
  }

  case class Between(lb: Double, ub: Double) extends StaticAnnotation

  object Between {
    implicit val repr: Repr[Between] =
      Repr.factory(a => List(("minimum", a.lb), ("maximum", a.ub)))
  }

}
