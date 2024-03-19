package schema

package annotations {

  case class Regex(regex: String) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("pattern", regex.toString))
  }

  case class MinLength(lb: Long) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("minLength", lb.toString))
  }

  case class MaxLength(ub: Long) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("maxLength", ub.toString))
  }

}
