package schema

import scala.annotation.StaticAnnotation

object annotations {

  case class Title(text: String) extends StaticAnnotation

  object Title {
    implicit val repr: Repr[Title] = Repr.factory(a => List(("title", a.text)))
  }

}
