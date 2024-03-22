package schema

import cats.syntax.all._
import io.circe.Json
import io.circe.JsonObject
import io.circe.syntax._
import schema.syntax._

import scala.reflect.macros.blackbox.Context

// See: https://users.scala-lang.org/t/how-to-use-data-structures-referencing-context-in-macros/3174
class SchemaFactory[C <: Context](c: C, ap: AnnotationParser) {

  import c.universe._

  private[this] def sanitizeParamName(name: String): Either[String, String] =
    name.split('.').lastOption.toEither(s"Unable to sanitize param name $name")

  private[this] def jsFromSymbolAnnotations(s: c.Symbol): Either[String, Json] =
    s.annotations
      .map(ap.parse)
      .sequence match {
      case Nil => JsonObject.empty.asJson.asRight
      case all => all.map(_.map(_.toJs).reduce(_ :+: _))
    }

  private[this] def paramJs(ps: c.Symbol): Either[String, Json] = {
    val tpe = ps.typeSignature
    val name = ps.fullName

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
      else if (t.weak_<:<(tpeDouble))
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
        t.typeSymbol.asClass.knownDirectSubclasses.toList
          .map(s => sanitizeParamName(s.fullName))
          .sequence
          .map(_.asJson)
          .map(js => List("enum" -> js))
      else s"Unsupported type $t".asLeft

    for {
      jsFromTpe <- tpeHelper(tpe).map(_.map(Json.obj(_)).reduce(_ :+: _))
      jsFromAnnotations <- jsFromSymbolAnnotations(ps)
      paramName <- sanitizeParamName(name)
    } yield Json.obj(paramName -> (jsFromTpe :+: jsFromAnnotations))

  }

  def meta(t: c.Type): Either[String, Json] =
    jsFromSymbolAnnotations(t.typeSymbol)
      .leftMap(err => s"Failed to get Jschema meta: $err")

  def required(t: c.Type): Either[String, Json] =
    t.members
      .filterNot(_.isMethod)
      .map(s => (s.fullName, s.typeSignature))
      .filterNot { case (_, t) => t <:< typeOf[Option[_]] }
      .map { case (n, _) => sanitizeParamName(n) }
      .toList
      .sequence
      .map(required => Json.obj("required" -> required.asJson))
      .leftMap(err => s"Failed to get the `required` section: $err")

  def properties(t: c.Type): Either[String, Json] =
    t.typeSymbol.asClass.primaryConstructor.typeSignature.paramLists.flatten
      .map(paramJs)
      .sequence
      .map(_.reduce(_ :+: _))
      .map(js => Json.obj("properties" -> js))
      .leftMap(err => s"Failed to get the `properties` section: $err")
}
