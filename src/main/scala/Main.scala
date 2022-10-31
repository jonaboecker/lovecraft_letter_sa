package scala

//val eol = sys.props("line.separator")
//def bar() = (("+" + "-" * 5) * 5) + "+" + eol

@main def run: Unit =
  println("Welcome to LovecraftLetter")
  val controller = new Controller
  controller.runLL
  // val tempVec = Vector(1, 1, 1)
  // print(new Board(tempVec))
  // getInputAndPrintLoop()
