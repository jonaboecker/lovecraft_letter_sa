FROM sbtscala/scala-sbt:graalvm-community-22.0.0_1.9.9_3.3.3

EXPOSE 8083

WORKDIR /effectHandler
ADD . /effectHandler

CMD sbt "runMain de.htwg.lovecraftletter.controller.effectHandler.effectHandlerRun"