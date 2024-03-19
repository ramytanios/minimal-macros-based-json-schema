package schema.examples

import schema.annotations._
import schema.JsonSchema

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

  println(JsonSchema.schema[Employee])

}
