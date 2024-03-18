package schema

trait Repr[A] {
  def repr(a: A): List[(String, String)]
}

object Repr {

  def apply[A](implicit ev: Repr[A]): Repr[A] = ev

  def factory[A](f: A => (String, String)): Repr[A] = new Repr[A] {
    def repr(a: A): List[(String, String)] = List(f(a))
  }

  def factoryL[A](f: A => List[(String, String)]): Repr[A] = new Repr[A] {
    def repr(a: A): List[(String, String)] = f(a)
  }

}
