FROM gradle:jdk11 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build


FROM openjdk:11-jdk-slim-buster

WORKDIR /home/kittybot

ADD /home/gradle/src/build/libs/KittyBot-*.jar KittyBot.jar

CMD ["java", "-jar", "KittyBot.jar"]
