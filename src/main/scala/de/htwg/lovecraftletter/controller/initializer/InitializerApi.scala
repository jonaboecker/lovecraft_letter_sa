package de.htwg.lovecraftletter.controller.initializer

import java.lang.Thread.sleep

object InitializerApi {
  @main def initializerRun(): Unit = {
    val initializer = Initializer()
    println("Initializer ist running... Strg + c to exit.")
    while (true) {
      sleep(100000)
    }
  }
}
