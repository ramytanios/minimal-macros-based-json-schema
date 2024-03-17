package schema

import scala.annotation.StaticAnnotation

object annotations {

  case class Title(text: String) extends StaticAnnotation

}
