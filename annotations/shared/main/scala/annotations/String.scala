package schema

import scala.annotation.StaticAnnotation

package annotations {

  case class Regex(regex: String) extends StaticAnnotation

  case class MinLength(lb: Long) extends StaticAnnotation

  case class MaxLength(ub: Long) extends StaticAnnotation

}
