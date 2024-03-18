package schema

import scala.annotation.StaticAnnotation

package annotations {

  case class Title(text: String) extends StaticAnnotation

  object Title {
    implicit val repr: Repr[Title] =
      Repr.factory((a: Title) => ("title", a.text))
  }

}
