package schema

import io.circe.JsonObject

object syntax {

  implicit class JsonObjectOps(private val js: JsonObject) extends AnyVal {
    def :+:(otherJs: JsonObject): JsonObject = js.deepMerge(otherJs)
  }

}
