package schema

import cats.syntax.all._
import io.circe.JsonObject
import io.circe.syntax._
import schema.syntax._

import scala.reflect.macros.blackbox.Context
import schema.annotations.CustomAnnotation

// See: https://users.scala-lang.org/t/how-to-use-data-structures-referencing-context-in-macros/3174
class SchemaFactory[C <: Context](c: C, ap: AnnotationParser, skipAnnotations: List[String]) {

  import c.universe._

  private[this] def sanitizeParamName(name: String): Either[String, String] =
    name.split('.').lastOption.toRight(s"Unable to sanitize param name $name")

  private[this] def jsFromSymbolAnnotations(s: c.Symbol): Either[String, JsonObject] =
    s.annotations
      .filterNot(ann =>
        sanitizeParamName(ann.tree.tpe.typeSymbol.fullName).exists(skipAnnotations.contains)
      ) match {
      case Nil => JsonObject.empty.asRight[String]
      case annotations =>
        annotations
          .map(ap.parse(c))
          .collect { // skip all annotations that are not `CustomAnnotation`
            case Right(Some(a)) => a.asRight
            case Left(e)        => e.asLeft
          }
          .sequence
          .map(_.map(_.repr))
          .map(_.reduce(_ :+: _))
    }

  private[this] def jsFromParamSymbol(ps: c.Symbol): Either[String, JsonObject] = {
    val tpe = ps.typeSignature
    val name = ps.fullName

    println(ps.annotations)

    def isNewtype(ps: c.Symbol): Boolean = 
      ps.annotations.contains("NewtypeInt") .....

    val tpeString = typeOf[String]
    val tpeDouble = typeOf[Double]
    val tpeOption = typeOf[Option[_]]
    val tpeSeq = typeOf[Seq[_]]
    val tpeJavaLocalDate = typeOf[java.time.LocalDate]
    val tpeJavaInstant = typeOf[java.time.Instant]
    val tpeBoolean = typeOf[Boolean]
    val tpeUUID = typeOf[java.util.UUID]

    def tpeHelper(t: Type): Either[String, JsonObject] =
      if (t =:= tpeString)
        JsonObject("type" -> "string".asJson).asRight
      else if (t =:= tpeUUID)
        JsonObject("type" -> "string".asJson, "format" -> "uuid".asJson).asRight
      else if (t =:= tpeJavaLocalDate)
        JsonObject("type" -> "string".asJson, "format" -> "date".asJson).asRight
      else if (t =:= tpeJavaInstant)
        JsonObject("type" -> "string".asJson, "format" -> "date-time".asJson).asRight
      else if (t.weak_<:<(tpeDouble))
        JsonObject("type" -> "number".asJson).asRight
      else if (t =:= tpeBoolean)
        JsonObject("type" -> "boolean".asJson).asRight
      else if (t.typeArgs.size == 1 && t <:< tpeSeq)
        JsonObject("type" -> "array".asJson).asRight
      else if (t.typeArgs.size == 1 && t <:< tpeOption)
        t.typeArgs.headOption
          .toRight("Failed to get `Option` type arguments")
          .flatMap(tpeHelper)
      else if (
        t.typeSymbol.isClass && t.typeSymbol.asClass.isTrait && t.typeSymbol.asClass.isSealed
      )
        t.typeSymbol.asClass.knownDirectSubclasses.toList
          .map(s => sanitizeParamName(s.fullName))
          .sequence
          .map(js => JsonObject("enum" -> js.asJson))
      else if (t.typeSymbol.asClass.isCaseClass)
        schema(t)
      else s"Unsupported type $t".asLeft

    for {
      jsFromTpe <- tpeHelper(tpe)
      jsFromAnnotations <- jsFromSymbolAnnotations(ps)
      paramName <- sanitizeParamName(name)
    } yield JsonObject(paramName -> (jsFromTpe :+: jsFromAnnotations).asJson)

  }

  def meta(t: c.Type): Either[String, JsonObject] =
    jsFromSymbolAnnotations(t.typeSymbol)
      .map(_ :+: JsonObject("type" -> "object".asJson))
      .leftMap(err => s"Failed to get schema meta: $err")

  def required(t: c.Type): Either[String, JsonObject] =
    t.members
      .filterNot(_.isMethod)
      .map(s => (s.fullName, s.typeSignature))
      .filterNot { case (_, t) => t <:< typeOf[Option[_]] }
      .map { case (n, _) => sanitizeParamName(n) }
      .toList
      .sequence
      .map(required => JsonObject("required" -> required.asJson))
      .leftMap(err => s"Failed to get the `required` section: $err")

  def properties(t: c.Type): Either[String, JsonObject] =
    t.typeSymbol.asClass.primaryConstructor.typeSignature.paramLists.flatten
      .map(jsFromParamSymbol)
      .sequence
      .map(_.reduce(_ :+: _))
      .map(js => JsonObject("properties" -> js.asJson))
      .leftMap(err => s"Failed to get the `properties` section: $err")

  def schema(t: c.Type): Either[String, JsonObject] =
    for {
      reqJs <- required(t)
      metaJs <- meta(t)
      propsJs <- properties(t)
    } yield reqJs :+: metaJs :+: propsJs

}
