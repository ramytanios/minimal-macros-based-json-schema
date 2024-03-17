package example

import scala.annotation.StaticAnnotation
object Info extends App {

  // annotations
  case class Title(t: String) extends StaticAnnotation
  case class Description(t: String) extends StaticAnnotation

  sealed trait Position
  object Position {
    case object Junior extends Position
    case object Senior extends Position
  }

  @Title("Employee title")
  case class Employee(
      @Title("Name title") @Description("Name desc") name: String = "ramy",
      @Title("Age title") @Description("Age desc") age: Int = 28,
      position: Position = Position.Junior
  )

  
  println(Macro.schema[Employee])

}
