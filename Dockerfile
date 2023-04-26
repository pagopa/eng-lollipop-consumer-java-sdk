FROM eclipse-temurin:11-jdk-alpine as build

WORKDIR /build
COPY ./samples/spring .

RUN --mount=type=secret,id=GITHUB_TOKEN \
  --mount=type=secret,id=GITHUB_ACTOR \
   export GITHUB_TOKEN=$(cat /run/secrets/GITHUB_TOKEN) && \
   export GITHUB_ACTOR=$(cat /run/secrets/GITHUB_ACTOR)

RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM eclipse-temurin:11-jdk-alpine as runtime

WORKDIR /app
COPY --from=build /build/build/libs/*.jar /app/app.jar

RUN addgroup -S appuser && adduser -S appuser -G appuser
USER appuser

EXPOSE 8080
ENTRYPOINT [ "java","-jar","/app/app.jar" ]