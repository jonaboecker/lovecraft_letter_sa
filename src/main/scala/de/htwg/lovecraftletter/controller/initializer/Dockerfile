FROM sbtscala/scala-sbt:graalvm-community-22.0.0_1.9.9_3.3.3

EXPOSE 8082

WORKDIR /initializer
ADD . /initializer

CMD sbt "runMain de.htwg.lovecraftletter.controller.initializer.initializerRun"