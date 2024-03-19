package schema

import cats.syntax.all._
import io.circe.Json
import io.circe.syntax._

trait SchemaFactory {

  type Context = scala.reflect.macros.blackbox.Context

  def required(c: Context)(t: c.Type): Either[String, Json] = {
    import c.universe._

    t.members
      .filterNot(_.isMethod)
      .map(s => (s.fullName, s.typeSignature))
      .filterNot { case (_, t) => t <:< typeOf[Option[_]] }
      .map { case (n, _) => n.split('.').lastOption }
      .toList
      .sequence
      .map(required => Json.obj("required" -> required.asJson))
      .fold("Unable to get required fields".asLeft[Json])(_.asRight[String])
  }

  def meta(c: Context)(t: c.Type)(ap: AnnotationParser): Either[String, Json] =
    t.typeSymbol.annotations
      .map(a => ap.parse(c)(a.tree.tpe.typeSymbol, a.tree.children.tail))
      .sequence
      .map(_.flatMap(_.repr))
      .map(_.map { case (a, b) => Json.obj(a -> b.asJson) })
      .map(_.reduce((ljs, rjs) => ljs.deepMerge(rjs)))
      .leftMap(s => s"Failed to get JSON schema meta: $s")

}
