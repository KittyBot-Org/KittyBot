FROM adoptopenjdk/openjdk11-openj9:alpine

WORKDIR /home/kittybot

COPY build/libs/KittyBot-all.jar KittyBot.jar

RUN apk update && apk upgrade && apk add curl

ENTRYPOINT ["java", "-jar", "KittyBot.jar"]
CMD ["-Xmx1G"]
