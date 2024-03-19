package schema

package annotations {

  case class MinItems(n: Long) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("minItems", n.toString))
  }

  case class MaxItems(n: Long) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("maxItems", n.toString))
  }

  case class Unique() extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("uniqueItems", true.toString))
  }

}
