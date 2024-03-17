package schema

import scala.annotation.StaticAnnotation

package annotations {

  case class MinItems(n: Long) extends StaticAnnotation

  case class MaxItems(n: Long) extends StaticAnnotation
  
  case class Unique() extends StaticAnnotation

}
