package schema.annotations

import scala.annotation.StaticAnnotation

trait CustomAnnotation extends  StaticAnnotation {
  def repr: List[(String, String)]
}
