package schema.demo

import schema.annotations._

object Main extends App {

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

  
  println(JsonSchema.schema[Employee])

}
