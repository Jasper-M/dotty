package scala.meta

/** Provides methods to unpickle `Expr` and `Type` trees. */
object Unpickler {

  /** Representation of pickled trees. For now it's String, but it
   *  should be changed to some kind of TASTY bundle.
   */
  type Pickled = String

  /** Unpickle `repr` which represents a pickled `Expr` tree,
   *  replacing splice nodes with `args`
   */
  def unpickleExpr[T](repr: Pickled, args: Seq[Expr[_]]): Expr[T] = ???

  /** Unpickle `repr` which represents a pickled `Type` tree,
   *  replacing splice nodes with `args`
   */
  def unpickleType[T](repr: Pickled, args: Seq[Type[_]]): Type[T] = ???
}
