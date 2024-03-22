package schema

import cats.syntax.all._
import schema.annotations.CustomAnnotation
import scala.reflect.macros.blackbox.Context

class AnnotationParser[C <: Context](c: C) {
  
  import c.universe._
  
  def parse(a: c.universe.Annotation): Either[String, CustomAnnotation] = {
    
    val annSymbol = a.tree.tpe.typeSymbol
    val annParams = a.tree.children.tail

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
