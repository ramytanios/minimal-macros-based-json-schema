package schema

import io.circe.Json
import io.circe.syntax._
import schema.annotations.CustomAnnotation

object syntax {

  implicit class JsonOps(private val js: Json) extends AnyVal {
    def :+:(otherJs: Json): Json = js.deepMerge(otherJs)
  }

  implicit class OptionOps[V](private val op: Option[V]) extends AnyVal {
    def toEither[L](left: L): Either[L, V] =
      op match {
        case Some(v) => Right[L, V](v)
        case None    => Left[L, V](left)
      }
  }

  implicit class CustomAnnotationOps(private val ca: CustomAnnotation) extends AnyVal {
    def toJs: Json = ca.repr.map { case (k, v) => Json.obj(k -> v.asJson) }.reduce(_ :+: _)
  }

}
