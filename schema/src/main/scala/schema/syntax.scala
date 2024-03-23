package schema

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

}
