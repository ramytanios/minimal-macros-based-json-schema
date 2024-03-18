import io.circe.Json
import scala.reflect.macros.blackbox

object JsonSchema {

  def schema[T]: Json = macro schemaMacro[T]

  def schemaMacro[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[Json] = {
    import c.universe._

    reify {
      Json.fromDouble(1).get
    }

  }
}
