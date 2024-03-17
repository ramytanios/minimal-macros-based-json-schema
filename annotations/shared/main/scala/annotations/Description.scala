package schema

import scala.annotation.StaticAnnotation

object annotations {

  case class Description(text: String) extends StaticAnnotation

  object Description {

    implicit val repr: Repr[Description] = Repr.factory(() => ("description", text))

  }

}
