package schema

package annotations {
  import io.circe.JsonObject
  import io.circe.syntax._

  case class Description(text: String) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("description" -> text.asJson)
  }

}
