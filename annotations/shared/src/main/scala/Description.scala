package schema

import scala.annotation.StaticAnnotation

package annotations {

  case class Description(text: String) extends StaticAnnotation

  object Description {
    implicit val repr: Repr[Description] =
      Repr.factory((a: Description) => ("description", a.text))
  }

}
