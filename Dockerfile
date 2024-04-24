FROM hseeberger/scala-sbt:11.0.12_1.5.5_2.13.6
#FROM sbtscala/scala-sbt:graalvm-community-22.0.0_1.9.9_3.3.3
RUN apt-get update && \
    apt-get install -y --no-install-recommends openjfx libxrender1 libxtst6 libxi6 xorg x11-xserver-utils && \
    rm -rf /var/lib/apt/lists/* \
EXPOSE 8081
EXPOSE 8080
ENV DISPLAY=host.docker.internal:0.0
WORKDIR /lovecraft_letter
ADD . /lovecraft_letter
CMD sbt run
CMD sbt "runMain de.htwg.lovecraftletter.run"
