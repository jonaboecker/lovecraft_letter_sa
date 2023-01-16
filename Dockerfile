FROM sbtscala/scala-sbt:openjdk-17.0.2_1.8.0_3.2.1 as builder
RUN apt-get install libxrender1:i386 libxtst6:i386 libxi6:i386
ENV DISPLAY :10
WORKDIR /lovecraft_letter
ADD . /lovecraft_letter
CMD sbt run