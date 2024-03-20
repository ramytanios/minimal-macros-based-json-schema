package schema

import io.circe.Json

object syntax {

  implicit class JsonOps(private val js: Json) extends AnyVal {
    def :+:(otherJs: Json): Json = js.deepMerge(otherJs)
  }

}
