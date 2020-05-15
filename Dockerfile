FROM gradle:jdk11 as build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --warning-mode all


FROM openjdk:11-alpine

WORKDIR /home/kittybot

COPY --from=build /home/gradle/src/build/libs/KittyBot-*.jar KittyBot.jar

ENTRYPOINT ["java"]
CMD ["-jar", "KittyBot.jar"]