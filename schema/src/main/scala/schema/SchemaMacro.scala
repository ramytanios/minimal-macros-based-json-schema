package schema

import io.circe.Json
import schema.syntax._

import scala.reflect.macros.blackbox

object SchemaMacro {

  def schema[T]: Json = macro schemaMacro[T]

  def schemaMacro[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[Json] = {

    import c.universe._

    val tpe = weakTypeOf[T]

    val ap = new AnnotationParser[c.type](c)
    val sf = new SchemaFactory[c.type](c, ap)

    def jsE: Either[String, Json] = for {
      reqJs <- sf.required(tpe)
      metaJs <- sf.meta(tpe)
      propsJs <- sf.properties(tpe)
    } yield reqJs :+: metaJs :+: propsJs

    if (!tpe.typeSymbol.asClass.isCaseClass)
      c.abort(
        c.enclosingPosition,
        s"Unsupported type `${weakTypeOf[T].typeSymbol.fullName}`. Only supports case class"
      )
    else
      jsE match {
        case Left(err) => c.abort(c.enclosingPosition, s"Failed to generate JSON schema: $err")
        case Right(js) => c.Expr[Json](q"io.circe.parser.parse(${js.toString()}).toOption.get")
      }

  }
}
