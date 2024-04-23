package de.htwg.lovecraftletter.controller.effectHandler

import java.lang.Thread.sleep

object effectHandlerApi {
  @main def run(): Unit = {
    val effectHandler = EffectHandler()
    println("press any key to exit...")
    while (true) {
      sleep(100000)
    }
  }
}
