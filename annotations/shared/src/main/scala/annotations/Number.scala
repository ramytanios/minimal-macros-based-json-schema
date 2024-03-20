package schema

package annotations {

  case class GreatherThan(lb: Double) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("minimum", lb.toString))
  }

  case class LessThan(ub: Double) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("maximum", ub.toString))
  }

  case class Between(lb: Double, ub: Double) extends CustomAnnotation {
    override def repr: List[(String, String)] =
      List(("minimum", lb.toString), ("maximum", ub.toString))
  }

}
