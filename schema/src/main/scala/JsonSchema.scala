package schema

import io.circe.Json
import io.circe.syntax._
import scala.reflect.macros.blackbox
import cats.syntax.all._

object JsonSchema {

  def schema[T]: Json = macro schemaMacro[T]

  def schemaMacro[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[Json] = {

    import c.universe._

    val t: Type = weakTypeOf[T]

    implicit val l: c.universe.Liftable[Json] =
      Liftable((in: Json) => q"io.circe.parser.parse(${in.toString()}).toOption.get")

    val requiredE: Either[String, List[String]] = t.members
      .filterNot(_.isMethod)
      .filterNot(s => s.asTerm.isParamWithDefault)
      .map(s => (s.fullName, s.typeSignature))
      .toList
      .filterNot { case (_, t) => t <:< typeOf[Option[_]] }
      .map { case (n, _) => n.split('.').lastOption }
      .sequence
      .fold("Unable to get required fields".asLeft[List[String]])(_.asRight[String])

    val js = for {
      required <- requiredE
    } yield Json.obj("required" -> required.asJson)

    js match {
      case Left(err) => c.abort(c.enclosingPosition, s"Failed to generate JSON schema: $err")
      case Right(j)  => c.Expr[Json](q"$j")
    }

  }
}
