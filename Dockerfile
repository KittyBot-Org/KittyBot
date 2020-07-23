FROM openjdk:11-jdk-slim-buster

WORKDIR /home/kittybot

COPY build/libs/KittyBot-*-all.jar KittyBot.jar

RUN apt update && apt -y install curl

ENTRYPOINT ["java"]
CMD ["-jar", "KittyBot.jar"]
