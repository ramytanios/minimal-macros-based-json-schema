package schema

import scala.annotation.StaticAnnotation

package annotations {

  case class GreatherThan(lb: Double) extends StaticAnnotation

  object GreatherThan {
    implicit val repr: Repr[GreatherThan] =
      Repr.factory((a: GreatherThan) => ("minimum", a.lb.toString))
  }

  case class LessThan(ub: Double) extends StaticAnnotation

  object LessThan {
    implicit val repr: Repr[LessThan] =
      Repr.factory((a: LessThan) => ("maximum", a.ub.toString))
  }

  case class Between(lb: Double, ub: Double) extends StaticAnnotation

  object Between {
    implicit val repr: Repr[Between] =
      Repr.factoryL((a: Between) =>
        List(("minimum", a.lb.toString), ("maximum", a.ub.toString))
      )
  }

}
