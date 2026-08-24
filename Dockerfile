FROM eclipse-temurin:11-jdk-alpine@sha256:6dce688b1b3fd39f4eb998a41a1b4851eae8a4a343aa0fb403118536244cbce5 as build

WORKDIR /build
COPY ./samples/spring .

FROM eclipse-temurin:11-jdk-alpine@sha256:6dce688b1b3fd39f4eb998a41a1b4851eae8a4a343aa0fb403118536244cbce5 as runtime

WORKDIR /app
COPY --from=build /build/build/libs/*.jar /app/app.jar
COPY --from=build /build/build/resources/main/application.properties /app/application.properties

RUN apk --update --no-cache add curl

RUN addgroup -S appuser && adduser -S appuser -G appuser
USER appuser

EXPOSE 8080
ENTRYPOINT [ "java","-jar","/app/app.jar", "/app/application.properties" ]