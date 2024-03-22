package schema

package annotations {

  case class Minimum(min: Double) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("minimum", min.toString))
  }

  case class Maximum(max: Double) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("maximum", max.toString))
  }

  case class ExclusiveMinimum(eMin: Double) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("exclusiveMinimum", eMin.toString))
  }

  case class ExclusiveMaximum(eMax: Double) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("exclusiveMaximum", eMax.toString))
  }

  case class MultipleOf(mul: Double) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("multipleOf" -> mul.toString))
  }

}
