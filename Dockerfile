FROM eclipse-temurin:11-jdk-alpine@sha256:046d9a602d147b86787a9523e03b1447020191fc164ac87bf4cfe17fec41539a as build

WORKDIR /build
COPY ./samples/spring .

FROM eclipse-temurin:11-jdk-alpine@sha256:046d9a602d147b86787a9523e03b1447020191fc164ac87bf4cfe17fec41539a as runtime

WORKDIR /app
COPY --from=build /build/build/libs/*.jar /app/app.jar
COPY --from=build /build/build/resources/main/application.properties /app/application.properties

RUN apk --update --no-cache add curl

RUN addgroup -S appuser && adduser -S appuser -G appuser
USER appuser

EXPOSE 8080
ENTRYPOINT [ "java","-jar","/app/app.jar", "/app/application.properties" ]