/*
rule = RemoveUnusedDefaultArgument
*/
package fix

object Main {
  private def foo(a: Int = 1, b: String = "b")(implicit ev: DummyImplicit): String = a + b
  private def bar(c: Int = 2, d: String = "d"): String = c + d
  def bazz(): Int = {
    def inner(x: Int = 1) = x
    inner(2)
  }
  foo(2)
  bar(d = "bar")
}
