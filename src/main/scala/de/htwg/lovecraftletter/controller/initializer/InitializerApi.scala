package de.htwg.lovecraftletter.controller.initializer

import java.lang.Thread.sleep

object InitializerApi {
  @main def initializerRun(): Unit = {
    val initializer = Initializer()
    while (true) {
      sleep(100000)
    }
  }
}
