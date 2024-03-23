package schema

package annotations {
  import io.circe.JsonObject
  import io.circe.syntax._

  case class MinItems(n: Long) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("minItems" -> n.asJson)
  }

  case class MaxItems(n: Long) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("maxItems" -> n.asJson)
  }

  case class Unique() extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("uniqueItems" -> true.asJson)
  }

}
