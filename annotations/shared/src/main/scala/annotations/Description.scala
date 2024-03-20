package schema

package annotations {

  case class Description(text: String) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("description", text))
  }

}
