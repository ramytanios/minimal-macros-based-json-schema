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
      @Title("Name") @Description("Name of employee") name: String = "ramy",
      @Title("Age") @Description("Age of employee") age: Int,
      @Description("Position of employee") position: Option[Position]
  )

  println(SchemaMacro.schema[Employee])

}
