FROM eclipse-temurin:11-jdk-alpine as build

WORKDIR /build
COPY ./samples/spring .

FROM eclipse-temurin:11-jdk-alpine as runtime

WORKDIR /app
COPY --from=build /build/build/libs/*.jar /app/app.jar
COPY --from=build /build/build/resources/main/application.properties /app/application.properties

RUN addgroup -S appuser && adduser -S appuser -G appuser
USER appuser

EXPOSE 8080
ENTRYPOINT [ "java","-jar","/app/app.jar", "/app/application.properties" ]