package schema.examples

import schema.SchemaMacro
import schema.annotations._

object Main extends App {

  sealed trait Position

  object Position {
    case object Junior extends Position
    case object Senior extends Position
  }

  sealed trait Location
  object Location {
    case object ZH extends Location
    case object SZ extends Location
    case object AG extends Location
  }

  // FIX: Change everything to JObject as it is clearer that its key values

  @Title("Company title")
  @Description("Company description")
  case class Company(@Title("Name") name: String, @Title("Location") location: Location)

  @Title("Employee title")
  @Description("Employee description")
  case class Employee(
      @Title("Name") @Description("Name of employee") name: String = "ramy",
      @Title("Age") @Description("Age of employee") age: Int,
      @Description("Position of employee") position: Option[Position],
      @Title("Company") company: Company
  )

  println(SchemaMacro.schema[Employee])

}
