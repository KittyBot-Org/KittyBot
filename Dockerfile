FROM adoptopenjdk/openjdk11-openj9:alpine

WORKDIR /home/kittybot

COPY build/libs/KittyBot-all.jar KittyBot.jar

RUN apk update && apk upgrade && apk add curl

ENV JAVA_OPTS="-Xmx1G -XX:+UseG1GC"

ENTRYPOINT java -jar $JAVA_OPTS KittyBot.jar
