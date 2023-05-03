# eng-lollipop-consumer-java-sdk

This repository contains the code that composes the `eng-lollipop-consumer-java-sdk`, containing the features to enable
a LolliPoP validation inside a Consumer project using the JVM environment. It contains:

- a main core module that defines the base structure for the validation process using the library
- several default implementations for the underlying integrations/functionalities
- samples using the sdk and the default implementations

## Modules

The repository contains the following Gradle modules, to be used within other projects:

| Module Artifact ID                      | Module Name                                                          | Description                                                                                                                                                                                                                                                                                                              |
|-----------------------------------------|----------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| __core__                                | [Core Module](core)                                                  | Core Library, containing the base services for LolliPop validation, and all references to the interfaces to be used.                                                                                                                                                                                                     |
| __http-verifier__                       | [Http Verifier Module](http-verifier)                                | Implementation of the core [HttpMessageVerifier](core/src/main/java/it/pagopa/tech/lollipop/consumer/http_verifier/HttpMessageVerifier.java) for the http-signature using [eng-http-signatures](https://github.com/pagopa/eng-http-signatures).                                                                          |
| __identity-service-rest-client-native__ | [Simple IdP Rest Client Module](identity-service-rest-client-native) | Implementation of [AssertionClientProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/client/AssertionClientProvider.java), defining a Rest client for Assertions recovery using JDK 11 standard functionalities of [Assertion OpenAPI specs](assertion-rest-client-native/openapi/openapi-spec.yml) |
| __assertion-rest-client-native__        | [Simple Assertion Rest Client Module](assertion-rest-client-native)  | Implementation of [AssertionClientProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/client/AssertionClientProvider.java), defining a Rest client for Assertions recovery using JDK 11 standard functionalities of [Assertion OpenAPI specs](assertion-rest-client-native/openapi/openapi-spec.yml) |
| __redis-storage__                       | [Redis Storage Module](redis-storage)                                | Contains implementation of the storage interfaces using Redis Cache.                                                                                                                                                                                                                                                     |
| __spring-impl__                         | [Spring Implementation Module](spring-impl)                          | Contains an implementation of a Spring Interceptor using the core modules validation commands, and provides a defined configuration for the core classes using Spring _@Configuration_ and _@ConfigurationProperties_                                                                                                    |

## Samples

| Sample Name                               | Description                                                                                                                                                                                                                                                                |
|-------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Simple](samples/simple)                  | Simple Application running default configurations and mocked services to test executing istances of LollipopConsumerCommand for validation                                                                                                                                 |
| [Spring with Interceptor](samples/spring) | Spring Boot Application sample defining Spring configurations of the standard modules through application.properties, using the spring-impl interceptor to validate request on the provided controller endpoint. Used in the Dockerfile in order to create an usable image |

## Use the SDK as dependency

The gradle modules contained in the repository (except the sources contained in the `sample` directory) will
be available on GitHub Packages. In order to include a dependency, use the Maven dependency

```xml
<dependency>
    <groupId>it.pagopa.tech.lollipop-consumer-java-sdk</groupId>
    <artifactId>MODULE_NAME</artifactId>
    <version>RELEASE_VERSION</version>
</dependency>
```
Or, in Gradle:

```
implementation 'it.pagopa.tech.lollipop-consumer-java-sdk:MODULE_NAME:RELEASE_VERSION'
```

Where `MODULE_NAME` is one of the modules to be used for the SDK (Described in the paragraph above), 
and `RELEASE_VERSION` is the version available on GitHub Packages (or the version installed locally).

## Local Installation

### Prerequisites

In order to work with the `eng-lollipop-consumer-java-sdk` locally the following are required to be installed:

- `JDK 11`
- `Gradle 7.4+`

### Maven Dependencies

In order to use maven it is required to have it installed locally, suggested to have it at least on version `3.8.5+`.

### Github Packages Dependencies

This library uses some dependencies available through GitHub Packages. It is required to have the following information,
in order to obtain them:

- `gpr.user` project property, or the `GITHUB_ACTOR` environment variable, containing the GitHub handle to be used for package recovery
- `gpr.key`  project property, or the `GITHUB_TOKEN` environment variable, containing the GitHub token to be used for package recovery.
The token must contain the permissions to retrieve GitHub Packages. 
See the [Official Guidelines](https://docs.github.com/en/packages/learn-github-packages/about-permissions-for-github-packages)

### Build Locally

In order to build the library source locally it is suggested to run the `.\gradlew build --refresh-dependecies`. 
Ensure that the properties regarding GitHub credentials have been defined beforehand. 

### Metadata Verification

This repository uses the Gradle verification process on dependency checksums, using the file provided
under `gradle\verification-metadata.xml`. Whenever the dependency check does exit with a failure locally, 
it might be necessary to update the file. Gradle does provide the command `.\gradlew --write-verification-metadata [algorithm] help` where
the algorithm could be sha256, or whenever it is not available for a particular dependency sha1. An already known
problem with the verification process and file update may not intercept all internal dependencies, obtaining
a different result from the automated checks within the GitHub workflow. Whenever an update is required, and the
provided cli command does not automatically update the file, it is required to introduce the checksum values manually.
See the official [Gradle Docs](https://docs.gradle.org/current/userguide/dependency_verification.html#sec:troubleshooting-verification)
for troubleshooting of the metadata verification.

### Publish to Maven local repository

In order to publish the modules on the local maven repository the command to be used is `.\gradlew publishToMavenLocal`

## Utilization

### Execute Validation Command

```
LollipopConsumerCommandBuilder commandBuilder = new LollipopConsumerCommandBuilderImpl(lollipopConsumerFactoryHelper);
commandBuilder.createCommand(lollipopConsumerRequest).doExecute();
```

