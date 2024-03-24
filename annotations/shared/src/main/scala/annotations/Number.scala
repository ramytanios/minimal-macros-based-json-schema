package schema

package annotations {

  import io.circe.JsonObject
  import io.circe.syntax._

  case class Minimum(min: Double) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("minimum" -> min.asJson)
  }

  case class Maximum(max: Double) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("maximum" -> max.asJson)
  }

  case class ExclusiveMinimum(eMin: Double) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("exclusiveMinimum" -> eMin.asJson)
  }

  case class ExclusiveMaximum(eMax: Double) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("exclusiveMaximum" -> eMax.asJson)
  }

  case class MultipleOf(mul: Double) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("multipleOf" -> mul.asJson)
  }

  case class Between(min: Double, max: Double = 2) extends CustomAnnotation {
    override def repr: JsonObject = JsonObject("min" -> min.asJson, "max" -> max.asJson)
  }

}
