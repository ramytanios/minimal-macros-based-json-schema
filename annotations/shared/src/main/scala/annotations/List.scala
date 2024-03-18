package schema

import scala.annotation.StaticAnnotation

package annotations {

  case class MinItems(n: Long) extends StaticAnnotation

  object MinItems {
    implicit val repr: Repr[MinItems] =
      Repr.factory((a: MinItems) => ("minItems", a.n.toString))
  }

  case class MaxItems(n: Long) extends StaticAnnotation

  object MaxItems {
    implicit val repr: Repr[MaxItems] =
      Repr.factory((a: MaxItems) => ("maxItems", a.n.toString))
  }

  case class Unique() extends StaticAnnotation

  object Unique {
    implicit val repr: Repr[Unique] =
      Repr.factory((_: Unique) => ("uniqueItems", true.toString))
  }

}
