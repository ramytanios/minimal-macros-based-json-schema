package schema

import io.circe.Json
import io.circe.syntax._
import scala.reflect.macros.blackbox
import cats.syntax.all._
import schema.annotations.CustomAnnotation

object JsonSchema {

  def schema[T]: Json = macro schemaMacro[T]

  def schemaMacro[T: c.WeakTypeTag](c: blackbox.Context): c.Expr[Json] = {

    import c.universe._

    val t: Type = weakTypeOf[T]

    def parseAnnotation(
        annSymbol: Symbol,
        annParams: List[Tree]
    ): Either[String, CustomAnnotation] = {

      val annClass = Class.forName(annSymbol.asClass.fullName)
      val constructor = annClass.getConstructors()(0)
      val paramsTypes = constructor.getParameterTypes

      def parseAnnParam(klass: Class[_], value: Tree): Either[String, Any] = {

        val lVal = value match {
          case Literal(Constant(lVal)) => lVal.toString.asRight
          case _                       => "Invalid literal for annotation".asLeft
        }

        lVal.flatMap(v =>
          if (klass == classOf[Double])
            v.toDoubleOption.fold("Invalid `Double`".asLeft[Any])(_.asRight)
          else if (klass == classOf[String])
            v.asRight[String]
          else if (klass == classOf[Int])
            v.toIntOption.fold("Invalid `Int`".asLeft[Any])(_.asRight)
          else if (klass == classOf[Boolean])
            v.toBooleanOption.fold("Invalid `Boolean`".asLeft[Any])(_.asRight)
          else value.toString.asRight[String]
        )

      }

      for {
        ctorParams <- paramsTypes.zipWithIndex
          .map { case (klass, idx) => parseAnnParam(klass, annParams(idx)) }
          .toList
          .sequence
        newInstance <- Either
          .catchNonFatal(constructor.newInstance(ctorParams: _*))
          .leftMap(_.getMessage)
        customAnn <- Either
          .catchNonFatal(newInstance.asInstanceOf[CustomAnnotation])
          .leftMap(_.getMessage)
      } yield customAnn

    }

    val requiredE: Either[String, Json] = t.members
      .filterNot(_.isMethod)
      .map(s => (s.fullName, s.typeSignature))
      .filterNot { case (_, t) => t <:< typeOf[Option[_]] }
      .map { case (n, _) => n.split('.').lastOption }
      .toList
      .sequence
      .map(required => Json.obj("required" -> required.asJson))
      .fold("Unable to get required fields".asLeft[Json])(_.asRight[String])

    val metaE: Either[String, Json] =
      t.typeSymbol.annotations
        .map(a => parseAnnotation(a.tree.tpe.typeSymbol, a.tree.children.tail))
        .sequence
        .map(_.flatMap(_.repr))
        .map(_.map { case (a, b) => Json.obj(a -> b.asJson) })
        .map(_.reduce((ljs, rjs) => ljs.deepMerge(rjs)))
        .leftMap(s => s"Failed to get JSON schema meta: $s")

    val jsE = for {
      required <- requiredE
      meta <- metaE
    } yield required deepMerge meta

    jsE match {
      case Left(err) => c.abort(c.enclosingPosition, s"Failed to generate JSON schema: $err")
      case Right(js) => c.Expr[Json](q"io.circe.parser.parse(${js.toString()}).toOption.get")
    }

  }
}
