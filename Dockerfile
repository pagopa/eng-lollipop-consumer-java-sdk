FROM eclipse-temurin:11-jdk-alpine@sha256:c057033c1ce71d23eb606c0db58eefaa6082ce2533f2d8f581c574aed16210cc as build

WORKDIR /build
COPY ./samples/spring .

FROM eclipse-temurin:11-jdk-alpine@sha256:c057033c1ce71d23eb606c0db58eefaa6082ce2533f2d8f581c574aed16210cc as runtime

WORKDIR /app
COPY --from=build /build/build/libs/*.jar /app/app.jar
COPY --from=build /build/build/resources/main/application.properties /app/application.properties

RUN apk --update --no-cache add curl

RUN addgroup -S appuser && adduser -S appuser -G appuser
USER appuser

EXPOSE 8080
ENTRYPOINT [ "java","-jar","/app/app.jar", "/app/application.properties" ]