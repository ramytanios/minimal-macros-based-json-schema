package schema

trait Repr[A] {
  def repr: (String, String)
}

object Repr {
  def apply[A](implicit ev: Repr[A]): Repr[A] = ev

  def factory[A](f: () => (String, String)): Repr[A] = new Repr[A] {
    def repr: (String, String) = f()
  }
}
