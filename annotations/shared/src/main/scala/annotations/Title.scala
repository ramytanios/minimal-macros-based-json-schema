package schema

package annotations {
  import io.circe.JsonObject
  import io.circe.syntax._

  import scala.annotation.Annotation

  case class Title(text: String) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("title" -> text.asJson)
  }

  case class Fishy(x: Int) extends Annotation

}
