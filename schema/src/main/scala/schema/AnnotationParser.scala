package schema

import cats.syntax.all._
import schema.annotations.CustomAnnotation

import scala.reflect.macros.blackbox.Context

class AnnotationParser() {

  def parse(c: Context)(a: c.universe.Annotation): Either[String, CustomAnnotation] = {

    import c.universe._

    val annSymbol = a.tree.tpe.typeSymbol
    val annParams = a.tree.children.tail

    def parseAnnParam(klass: Class[_], value: Tree): Either[String, Any] = {

      val lVal = value match {
        case Literal(Constant(lVal)) => lVal.toString.asRight
        case _                       => "Invalid literal for annotation".asLeft
      }

      lVal.flatMap(v =>
        if (klass == classOf[Double])
          v.toDoubleOption.fold(s"Invalid `Double` $v".asLeft[Any])(_.asRight)
        else if (klass == classOf[String])
          v.asRight[String]
        else if (klass == classOf[Int])
          v.toIntOption.fold(s"Invalid `Int` $v".asLeft[Any])(_.asRight)
        else if (klass == classOf[Boolean])
          v.toBooleanOption.fold(s"Invalid `Boolean` $v".asLeft[Any])(_.asRight)
        else s"Unsupported annotation field value type".asLeft[Any]
      )

    }

    for {
      annClass <- Either
        .catchNonFatal(Class.forName(annSymbol.asClass.fullName))
        .leftMap(_.getMessage)
      // _ <- .flatTap(_ =>
      //     c.warning(
      //       c.enclosingPosition,
      //       s"Annotation ${annSymbol.asClass.fullName} could not be found"
      //     ).asRight
      //   )
      constructor <- Either
        .catchNonFatal(annClass.getConstructors()(0))
        .leftMap(_.getMessage)
      paramsTypes <- Either
        .catchNonFatal(constructor.getParameterTypes)
        .leftMap(_.getMessage)
      ctorParams <- paramsTypes.zipWithIndex
        .map { case (klass, idx) => parseAnnParam(klass, annParams(idx)) }
        .toList
        .sequence
        .leftMap(err => s"Failed to parse annotation params: $err")
      newInstance <- Either
        .catchNonFatal(constructor.newInstance(ctorParams: _*))
        .leftMap(err => s"Failed to get new instance of annotation: $err")
      customAnn <- Either
        .catchNonFatal(newInstance.asInstanceOf[CustomAnnotation])
        .leftMap(_.getMessage)
        .leftMap(err => s"Failed to cast annotation to `CustomAnnotation`: $err")
    } yield customAnn

  }

}
