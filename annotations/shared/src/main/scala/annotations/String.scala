package schema

package annotations {
  import io.circe.JsonObject
  import io.circe.syntax._

  case class Regex(regex: String) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("pattern" -> regex.asJson)
  }

  case class MinLength(lb: Long) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("minLength" -> lb.asJson)
  }

  case class MaxLength(ub: Long) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("maxLength" -> ub.asJson)
  }

  case class Email() extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("format" -> "email".asJson)
  }

  case class Hostname() extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("format" -> "hostname".asJson)
  }

}
