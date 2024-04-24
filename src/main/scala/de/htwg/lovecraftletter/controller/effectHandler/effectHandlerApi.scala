package de.htwg.lovecraftletter.controller.effectHandler

import java.lang.Thread.sleep

object effectHandlerApi {
  @main def effectHandlerRun(): Unit = {
    val effectHandler = EffectHandler()
    println("Effect Handler ist running... Strg + c to exit.")
    while (true) {
      sleep(100000)
    }
  }
}
