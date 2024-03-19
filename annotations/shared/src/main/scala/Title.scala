package schema

package annotations {

  case class Title(text: String) extends CustomAnnotation {
    override def repr: List[(String, String)] = List(("title", text))
  }

}
