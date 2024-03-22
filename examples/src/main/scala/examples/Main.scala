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

  @Title("Company title")
  @Description("Company description")
  case class Company(
      @Title("Name")
      name: String,
      @Title("Location")
      location: Location
  )

  @Title("Employee title")
  @Description("Employee description")
  case class Employee(
      @Title("Name")
      @Description("Name of employee")
      name: String = "ramy",
      @Title("Age")
      @Description("Age of employee")
      age: Long,
      id: java.util.UUID,
      @Description("Position of employee")
      position: Option[Position],
      @Title("Company")
      company: Company,
      @MultipleOf(10.5)
      salary: Int
  )

  println(SchemaMacro.schema[Employee])

}
