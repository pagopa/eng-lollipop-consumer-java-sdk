FROM eclipse-temurin:11-jdk-alpine@sha256:d4e76d3c96624f1aee917150729a3ac0cd35f61390bb1935825461db1b2126a3 as build

WORKDIR /build
COPY ./samples/spring .

FROM eclipse-temurin:11-jdk-alpine@sha256:d4e76d3c96624f1aee917150729a3ac0cd35f61390bb1935825461db1b2126a3 as runtime

WORKDIR /app
COPY --from=build /build/build/libs/*.jar /app/app.jar
COPY --from=build /build/build/resources/main/application.properties /app/application.properties

RUN apk --update --no-cache add curl

RUN addgroup -S appuser && adduser -S appuser -G appuser
USER appuser

EXPOSE 8080
ENTRYPOINT [ "java","-jar","/app/app.jar", "/app/application.properties" ]