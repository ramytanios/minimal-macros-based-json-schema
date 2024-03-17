package schema

import scala.annotation.StaticAnnotation

object annotations {

  case class Description(text: String) extends StaticAnnotation

}
