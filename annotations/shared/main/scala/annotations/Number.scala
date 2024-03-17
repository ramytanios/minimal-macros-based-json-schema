package schema

import scala.annotation.StaticAnnotation

package annotations {

  case class GreatherThan(lb: Double) extends StaticAnnotation

  case class LessThan(ub: Double) extends StaticAnnotation

  case class Between(lb: Double, ub: Double) extends StaticAnnotation

}
