package schema

import cats.syntax.all._
import io.circe.Json
import io.circe.syntax._
import schema.syntax._
import io.circe.JsonObject

trait SchemaFactory {

  type Context = scala.reflect.macros.blackbox.Context

  private[this] def sanitizeParamName(name: String): Option[String] =
    name.split('.').lastOption

  def required(c: Context)(t: c.Type): Either[String, Json] = {
    import c.universe._

    t.members
      .filterNot(_.isMethod)
      .map(s => (s.fullName, s.typeSignature))
      .filterNot { case (_, t) => t <:< typeOf[Option[_]] }
      .map { case (n, _) => sanitizeParamName(n) }
      .toList
      .sequence
      .map(required => Json.obj("required" -> required.asJson))
      .fold("Unable to get required fields".asLeft[Json])(_.asRight[String])
  }

  def meta(c: Context)(t: c.Type)(ap: AnnotationParser): Either[String, Json] =
    t.typeSymbol.annotations
      .map(a => ap.parse(c)(a.tree.tpe.typeSymbol, a.tree.children.tail))
      .sequence
      .map(_.map(_.toJs).reduce(_ :+: _))
      .leftMap(s => s"Failed to get JSON schema meta: $s")

  private[this] def paramJs(
      c: Context
  )(ps: c.Symbol)(ap: AnnotationParser): Either[String, Json] = {
    import c.universe._

    val tpe = ps.typeSignature
    val name = ps.fullName
    val annotations = ps.annotations

    val tpeString = typeOf[String]
    val tpeDouble = typeOf[Double]
    val tpeOption = typeOf[Option[_]]
    val tpeSeq = typeOf[Seq[_]]
    val tpeJavaLocalDate = typeOf[java.time.LocalDate]
    val tpeJavaInstant = typeOf[java.time.Instant]
    val tpeBoolean = typeOf[Boolean]

    def tpeHelper(t: Type): Either[String, List[(String, Json)]] =
      if (t =:= tpeString)
        List("type" -> "string".asJson).asRight
      else if (t =:= tpeJavaLocalDate)
        List("type" -> "string".asJson, "format" -> "date".asJson).asRight
      else if (t =:= tpeJavaInstant)
        List("type" -> "string".asJson, "format" -> "date-time".asJson).asRight
      else if (t weak_<:< tpeDouble)
        List("type" -> "number".asJson).asRight
      else if (t =:= tpeBoolean)
        List("type" -> "boolean".asJson).asRight
      else if (t.typeSymbol.asClass.isCaseClass)
        List("type" -> "object".asJson).asRight
      else if (t.typeArgs.size == 1 && t <:< tpeSeq)
        List("type" -> "array".asJson).asRight
      else if (t.typeArgs.size == 1 && t <:< tpeOption)
        tpeHelper(t.typeArgs.head)
      else if (t.typeSymbol.asClass.isTrait && t.typeSymbol.asClass.isSealed)
        List(
          "enum" -> t.typeSymbol.asClass.knownDirectSubclasses
            .map(s => sanitizeParamName(s.fullName).get)
            .asJson
        ).asRight
      else s"Unsupported type $t".asLeft

    for {
      fromTpe0 <- tpeHelper(tpe)
      fromTpe = fromTpe0
        .map { case (n, v) => Json.obj(n -> v) }
        .reduce(_ :+: _)
      fromAnnotation <- annotations match {
        case Nil => JsonObject.empty.asJson.asRight[String]
        case all =>
          all
            .map(a => ap.parse(c)(a.tree.tpe.typeSymbol, a.tree.children.tail))
            .sequence
            .map(_.map(_.toJs).reduce(_ :+: _))
      }
    } yield Json.obj(sanitizeParamName(name).get -> (fromTpe :+: fromAnnotation))
  }

  def properties(c: Context)(t: c.Type)(ap: AnnotationParser): Either[String, Json] =
    t.typeSymbol.asClass.primaryConstructor.typeSignature.paramLists.flatten
      .map(paramSymbol => paramJs(c)(paramSymbol)(ap))
      .sequence
      .map(_.reduce(_ :+: _))
      .map(js => Json.obj("properties" -> js))
}
