package schema

package annotations {
  import io.circe.JsonObject
  import io.circe.syntax._

  case class NewtypeInt() extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("type" -> "number".asJson)
  }

}



