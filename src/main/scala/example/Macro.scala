package example

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import io.circe.Json

object Macro {

  def schema[T]: Json = macro schemaMacro[T]

  def schemaMacro[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[Json] = {
    import c.universe._

    reify {
      Json.fromDouble(1).get
    }

  }
}
