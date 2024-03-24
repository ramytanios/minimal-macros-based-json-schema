package schema

import cats.syntax.all._
import schema.annotations.CustomAnnotation

import scala.reflect.macros.blackbox.Context

class AnnotationParser() {

  def parse(c: Context)(a: c.universe.Annotation): Either[String, Option[CustomAnnotation]] = {

    import c.universe._

    val annSymbol = a.tree.tpe.typeSymbol
    val annParams = a.tree.children.tail

    def parseAnnParam(klass: Class[_], value: Tree): Either[String, Any] = {

      val lVal = value match {
        case Literal(Constant(lVal)) => lVal.toString.asRight
        case _ => s"Invalid literal for annotation with klass $klass and tree $value".asLeft
      }

      lVal.flatMap(v =>
        if (klass == classOf[Double])
          v.toDoubleOption.fold(s"Invalid `Double` $v".asLeft[Any])(_.asRight)
        else if (klass == classOf[String])
          v.asRight[String]
        else if (klass == classOf[Int])
          v.toIntOption.fold(s"Invalid `Int` $v".asLeft[Any])(_.asRight)
        else if (klass == classOf[Long])
          v.toLongOption.fold(s"Invalid `Long` $v".asLeft[Any])(_.asRight)
        else if (klass == classOf[Boolean])
          v.toBooleanOption.fold(s"Invalid `Boolean` $v".asLeft[Any])(_.asRight)
        else s"Unsupported annotation field value type".asLeft[Any]
      )

    }

    for {
      annClass <- Either
        .catchNonFatal(Class.forName(annSymbol.asClass.fullName))
        .leftMap(err => s"missing class: ${err.getMessage}")
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
      customAnn <- (Either
        .catchNonFatal(newInstance.asInstanceOf[CustomAnnotation]) match {
        case Left(_)  => None
        case Right(a) => Some(a)
      }).asRight
    } yield customAnn

  }

}
