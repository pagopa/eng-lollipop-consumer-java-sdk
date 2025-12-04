FROM eclipse-temurin:11-jdk-alpine@sha256:e763ac96a3deaada17b197bc253f3b8cbda63ed5bad91f93b1428001c9148a2e as build

WORKDIR /build
COPY ./samples/spring .

FROM eclipse-temurin:11-jdk-alpine@sha256:e763ac96a3deaada17b197bc253f3b8cbda63ed5bad91f93b1428001c9148a2e as runtime

WORKDIR /app
COPY --from=build /build/build/libs/*.jar /app/app.jar
COPY --from=build /build/build/resources/main/application.properties /app/application.properties

RUN apk --update --no-cache add curl

RUN addgroup -S appuser && adduser -S appuser -G appuser
USER appuser

EXPOSE 8080
ENTRYPOINT [ "java","-jar","/app/app.jar", "/app/application.properties" ]