package scala
//val eol = sys.props("line.separator")
//def bar() = (("+" + "-" * 5) * 5) + "+" + eol

@main def hello: Unit =
  println("Hello world!!")
  println(msg)
  // println(bar())
  // println(card)
  // println(card2)
  val tempVec = Vector(1, 1, 1)
  print(new Board(tempVec))

def msg = "I was compiled by Scala 3. :)"
