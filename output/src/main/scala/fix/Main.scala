package fix

object Main {
  private def foo(a: Int, b: String = "b")(implicit ev: DummyImplicit): String = a + b
  private def bar(c: Int = 2, d: String): String = c + d
  foo(2)
  bar(d = "bar")
}
