package schema.examples

import schema.SchemaMacro
import schema.annotations._

object Main extends App {

  sealed trait Position

  object Position {
    case object Junior extends Position
    case object Senior extends Position
  }

  @Title("Employee title")
  @Description("Employee description")
  case class Employee(
      @Title("Name title") @Description("Name desc") name: String = "ramy",
      @Title("Age title") @Description("Age desc") age: Int,
      position: Option[Position]
  )

  println(SchemaMacro.schema[Employee])

}
