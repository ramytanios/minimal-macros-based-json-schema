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

    implicit val l: c.universe.Liftable[Json] =
      Liftable((in: Json) => q"io.circe.parser.parse(${in.toString()}).toOption.get")

    def parseAnnotation(
        annSymbol: Symbol,
        annParams: List[Tree]
    ): Either[String, CustomAnnotation] = {

      val annClass = Class.forName(annSymbol.asClass.fullName)
      val constructor = annClass.getConstructors()(0)

      def parseAsDouble(v: Any) = Double.box(v.toString.toDouble)
      def parseAsInteger(v: Any) = Integer.valueOf(v.toString.toDouble.toInt)
      def parseAsBoolean(v: Any) = Boolean.box(v.toString.toBoolean)

      def parseAnnParam(klass: Class[_], value: Tree): AnyRef = {
        if (klass == classOf[Double]) parseAsDouble(value)
        else if (klass == classOf[String]) value.toString
        else if (klass == classOf[Int]) parseAsInteger(value)
        else if (klass == classOf[Boolean]) parseAsBoolean(value)
        else value.toString
      }

      val ctorParams = constructor.getParameterTypes.zipWithIndex.map { case (klass, idx) =>
        parseAnnParam(klass, annParams(idx))
      }

      Either
        .catchNonFatal(
          constructor.newInstance(ctorParams: _*).asInstanceOf[CustomAnnotation]
        )
        .leftMap(t => t.getMessage)

    }

    val requiredE: Either[String, List[String]] = t.members
      .filterNot(_.isMethod)
      .map(s => (s.fullName, s.typeSignature))
      .toList
      .filterNot { case (_, t) => t <:< typeOf[Option[_]] }
      .map { case (n, _) => n.split('.').lastOption }
      .sequence
      .fold("Unable to get required fields".asLeft[List[String]])(_.asRight[String])

    val metaE: Either[String, Json] =
      t.typeSymbol.annotations
        .map(a => parseAnnotation(a.tree.tpe.typeSymbol, a.tree.children.tail))
        .sequence
        .map(_.flatMap(_.repr))
        .map(_.map { case (a, b) => Json.obj(a -> b.asJson) })
        .map(_.reduce((ljs, rjs) => ljs.deepMerge(rjs)))

    val jsE = for {
      required <- requiredE
      meta <- metaE
    } yield Json.obj("required" -> required.asJson) deepMerge meta

    jsE match {
      case Left(err) => c.abort(c.enclosingPosition, s"Failed to generate JSON schema: $err")
      case Right(js) => c.Expr[Json](q"$js")
    }

  }
}
