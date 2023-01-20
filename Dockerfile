#FROM sbtscala/scala-sbt:openjdk-17.0.2_1.8.0_3.2.1 as builder
#FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1
FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1
ENV DISPLAY=host.docker.internal:0.0
RUN apt-get update && \
    apt-get install -y --no-install-recommends openjfx && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get install -y sbt libxrender1 libxtst6 libxi6
WORKDIR /lovecraft_letter
ADD . /lovecraft_letter
CMD sbt run