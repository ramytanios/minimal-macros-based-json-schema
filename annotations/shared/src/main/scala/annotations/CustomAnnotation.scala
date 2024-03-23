package schema.annotations

import io.circe.JsonObject

import scala.annotation.StaticAnnotation

trait CustomAnnotation extends StaticAnnotation {
  def repr: JsonObject
}
