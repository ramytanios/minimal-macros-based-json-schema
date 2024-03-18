package schema

trait Repr[A] {
  type R
  def repr(a: A): List[(String, R)]
}

object Repr {

  def apply[A](implicit ev: Repr[A]): Repr[A] = ev

  def factory[A, R](f: a => (String, R)): Repr[A] = new Repr[A] {
    def repr: (String, R) = f(a)
  }

}
