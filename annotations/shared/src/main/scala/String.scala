package schema

import scala.annotation.StaticAnnotation

package annotations {

  case class Regex(regex: String) extends StaticAnnotation

  object Regex {
    implicit val repr: Repr[Regex] =
      Repr.factory((a: Regex) => ("pattern", a.regex.toString))
  }

  case class MinLength(lb: Long) extends StaticAnnotation

  object MinLength {
    implicit val repr: Repr[MinLength] =
      Repr.factory((a: MinLength) => ("minLength", a.lb.toString))
  }

  case class MaxLength(ub: Long) extends StaticAnnotation

  object MaxLength {
    implicit val repr: Repr[MaxLength] =
      Repr.factory((a: MaxLength) => ("maxLength", a.ub.toString))
  }

}
