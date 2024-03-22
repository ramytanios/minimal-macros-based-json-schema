package schema

import io.circe.syntax._
import schema.annotations.CustomAnnotation
import io.circe.JsonObject

object syntax {

  implicit class JsonObjectOps(private val js: JsonObject) extends AnyVal {
    def :+:(otherJs: JsonObject): JsonObject = js.deepMerge(otherJs)
  }

  implicit class OptionOps[V](private val op: Option[V]) extends AnyVal {
    def toEither[L](left: L): Either[L, V] =
      op match {
        case Some(v) => Right[L, V](v)
        case None    => Left[L, V](left)
      }
  }

  implicit class CustomAnnotationOps(private val ca: CustomAnnotation) extends AnyVal {
    def toJs: JsonObject =
      ca.repr.map { case (k, v) => JsonObject(k -> v.asJson) }.reduce(_ :+: _)
  }

}
