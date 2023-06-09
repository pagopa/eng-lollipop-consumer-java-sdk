# eng-lollipop-consumer-java-sdk

This repository contains the code that composes the `eng-lollipop-consumer-java-sdk`, containing the features to enable
a LolliPoP validation inside a Consumer project using the JVM environment. It contains:

- a main core module that defines the base structure for the validation process using the library
- several default implementations for the underlying integrations/functionalities
- samples using the sdk and the default implementations

## Modules

The repository contains the following Gradle modules, to be used within other projects:

| Module Artifact ID                      | Module Name                                                          | Description                                                                                                                                                                                                                                                                                                                   |
|-----------------------------------------|----------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| __core__                                | [Core Module](core)                                                  | Core Library, containing the base services for LolliPop validation, and all references to the interfaces to be used.                                                                                                                                                                                                          |
| __http-verifier__                       | [Http Verifier Module](http-verifier)                                | Implementation of the core [HttpMessageVerifier](core/src/main/java/it/pagopa/tech/lollipop/consumer/http_verifier/HttpMessageVerifier.java) for the http-signature using [eng-http-signatures](https://github.com/pagopa/eng-http-signatures).                                                                               |
| __identity-service-rest-client-native__ | [Simple IdP Rest Client Module](identity-service-rest-client-native) | Implementation of [IdpCertClientProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/client/IdpCertClientProvider.java), defining a Rest client for IDP data recovery using JDK 11 standard functionalities of [Identity Services OpenAPI specs](identity-service-rest-client-native/openapi/openapi-spec.yml)   |
| __assertion-rest-client-native__        | [Simple Assertion Rest Client Module](assertion-rest-client-native)  | Implementation of [AssertionClientProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/client/AssertionClientProvider.java), defining a Rest client for Assertions recovery using JDK 11 standard functionalities of [Assertion OpenAPI specs](assertion-rest-client-native/openapi/openapi-spec.yml)      |
| __redis-storage__                       | [Redis Storage Module](redis-storage)                                | Contains implementation of the storage interfaces using Redis Cache.                                                                                                                                                                                                                                                          |
| __spring-impl__                         | [Spring Implementation Module](spring-impl)                          | Contains an implementation of a Spring Interceptor using the core modules validation commands, and provides a defined configuration for the core classes using Spring _@Configuration_ and _@ConfigurationProperties_                                                                                                         |
| __servlet-impl__                        | [Servlet Implementation Module](servlet-impl)                        | Contains an implementation of a servlet filter using the core modules validation commands                                                                                                         |

## Samples

| Sample Name                                    | Description                                                                                                                                                                                                                                                                |
|------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Simple](samples/simple)                       | Simple Application running default configurations and mocked services to test executing instances of LollipopConsumerCommand for validation                                                                                                                                |
| [Simple with Typesafe](samples/simpleTypesafe) | Simple Application running default configurations and mocked services to test executing instances of LollipopConsumerCommand for validation, configured with custom properties using LollipopTypesafeConfig utility class                                                  |
| [Spring with Interceptor](samples/spring)      | Spring Boot Application sample defining Spring configurations of the standard modules through application.properties, using the spring-impl interceptor to validate request on the provided controller endpoint. Used in the Dockerfile in order to create an usable image |
| [Simple with Servlet](samples/servlet)         | Simple Application sample running default configurations and mocked services, using the servlet filter to validate request on the provided servlet exposed through embedded Tomcat server |

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

### Native Build

In order to compile the modules contained in this repository in a native form, the graalvm plugin is applied. In order to execute
a native build it is required that an instance of the GraalVM JDK is installed and configured.

In order to compile natively execute this command:

`.\gradlew nativeCompile`

The result will be found under the module `build/native` directory.

Note that currently a native version of the SDK is not automatically published on the GitHub Package.

## Utilization

### Configurations

The core module contains defined parameters to configure the provided services. Within each of the configuration classes
provided in this module, that are not directly related to an implementation of a specific library or framework does not
contain any functionality for property loading. The choice of the preferred method to apply the configuration properties
is defined by the library user. The configuration classes are defined as follows:

#### [LollipopConsumerRequestConfig](core/src/main/java/it/pagopa/tech/lollipop/consumer/config/LollipopConsumerRequestConfig.java)

This configuration class contains the standard configuration properties for the SDK core functionalities:

| Property                          | Description                                                                                                                                                                                 | Default                                          |
|-----------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------|
| __strictDigestVerify__            | Flag to define if the digest validation is applied in strict-mode. when false if the content-digest header isn't present in the signature-input parameters to validate, it won't be checked | false                                            |                                          
| __signatureHeader__               | Header expected to contain the signatures to be validated                                                                                                                                   | signature                                        |
| __signatureInputHeader__          | Header expected to contain the signature-input to be used in signature validation                                                                                                           | signature-input                                  |
| __contentEncodingHeader__         | Header expected to contain the content encoding to be used when the request body is present.                                                                                                | content-encoding                                 |
| __contentDigestHeader__           | Header expected to contain the content digest to be validated                                                                                                                               | content-digest                                   |
| __originalMethodHeader__          | Header expected to contain the original method calling the API                                                                                                                              | x-pagopa-lollipop-original-method                |
| __originalURLHeader__             | Header expected to contain the original url calling the API                                                                                                                                 | x-pagopa-lollipop-original-url                   |
| __assertionRefHeader__            | Header expected to contain the assertion-ref to be used in order to retrieve the SAML Assertion to validate                                                                                 | x-pagopa-lollipop-assertion-ref                  |
| __assertionTypeHeader__           | Header expected to contain the assertion type                                                                                                                                               | x-pagopa-lollipop-assertion-type                 |
| __userIdHeader__                  | Header expected to contain the user-id to be used in SAML validation                                                                                                                        | x-pagopa-lollipop-user-id                        |
| __publicKeyHeader__               | Header expected to contain the LolliPop public key to be used for the validation process                                                                                                    | x-pagopa-lollipop-public-key                     |
| __authJWTHeader__                 | Header expected to contain the JWT to be used in SAML Assertion retrieval                                                                                                                   | x-pagopa-lollipop-auth-jwt                       |
| __expectedFirstLcOriginalMethod__ | The expected value within the originalMethodHeader, used in formal validation of the request to be processed                                                                                | POST                                             |
| __expectedFirsxtLcOriginalUrl__   | The expected value within the originalURLHeader, used in formal validation of the request to be processed                                                                                   | https://api-app.io.pagopa.it/first-lollipop/sign |
| __assertionExpireInDays__         | Number of days to be consider when checking if the difference between the current date and the NotBefore value in the SAML Assertion                                                        | 30                                               |
| __assertionNotBeforeDateFormat__  | Date format to be used for parsing the NotBeforeDate in the SAML Assertion                                                                                                                  | yyyy-MM-dd'T'HH:mm:ss.SSS'Z'                     |
| __assertionInstantDateFormat__    | Date format to be used for parsing the InstantDate in the SAML Assertion                                                                                                                    | yyy-MM-dd'T'HH:mm:ss.SSS'Z'                      |
| __samlNamespaceAssertion__        | SAML Namespace of the Assertion                                                                                                                                                             | urn:oasis:names:tc:SAML:2.0:assertion            |
| __assertionNotBeforeTag__         | Tag containing the NotBefore value in the SAML Assertion                                                                                                                                    | Conditions                                       |
| __assertionFiscalCodeTag__        | Tag containing the FiscalCode value in the SAML Assertion                                                                                                                                   | Attribute                                        |
| __assertionInResponseToTag__      | Tag containing the InResponseTo value in the SAML Assertion                                                                                                                                 | SubjectConfirmationData                          |
| __assertionEntityIdTag__          | Tag containing the EntityId value in the SAML Assertion                                                                                                                                     | Issuer                                           |
| __assertionInstantTag__           | Tag containing the Instant value in the SAML Assertion                                                                                                                                      | Assertion                                        |
| __enableConsumerLogging__         | Flag enabling the logging of the request validation, using the implementation of the provided interface                                                                                     | true                                             |
| __enableAssertionLogging__        | Flag enabling the logging the retrieved SAML Assertion for audit purposes, using the implementation of the provided interface                                                               | true                                             |
| __enableIdpCertDataLogging__      | Flag enabling the logging the retrieved IdP certificates for audit purposes, using the implementation of the provided interface                                                             | true                                             |

#### [AssertionStorageConfig](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/storage/StorageConfig.java)

This configuration contains the standard properties regarding the storage interface for the SAML Assertion data:

| Property                         | Description                                                                                                                      | Default |
|----------------------------------|----------------------------------------------------------------------------------------------------------------------------------|---------|
| __assertionStorageEnabled__      | Flag to determine if the implementation of the storage interface regarding the SAML Assertion will be used for retrieval/storage | true    |
| __storageEvictionDelay__         | Eviction delay value                                                                                                             | 1       |
| __storageEvictionDelayTimeUnit__ | Time unit to be used when defining the delay regarding the eviction                                                              | MINUTES |
| __maxNumberOfElements__          | Maximum number of Assertion allows to be stored                                                                                  | 100     |

#### [IdpCertStorageConfig](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/storage/IdpCertStorageConfig.java)

This configuration contains the standard properties regarding the storage interface for the IdP certificates:

| Property                         | Description                                                                                                                        | Default |
|----------------------------------|------------------------------------------------------------------------------------------------------------------------------------|---------|
| __idpCertDataStorageEnabled__    | Flag to determine if the implementation of the storage interface regarding the IdP Certificates will be used for retrieval/storage | true    |
| __storageEvictionDelay__         | Eviction delay value                                                                                                               | 1       |
| __storageEvictionDelayTimeUnit__ | Time unit to be used when defining the delay regarding the eviction                                                                | MINUTES |

#### Other Configs

Inside the modules with the default implementations of the core interfaces regarding storage, rest clients and signature
validation functionalities, are contained configuration classes specific for the provided implementations, the description
of which are defined in the respective Readme files.

### Implementations Usage

Each service inside the core SDK is defined by an interface and a related Factory to generate an instance of the service,
using the related configurations. The repository contains default implementations for each one of the interfaces to be used.
The following steps should be followed to ensure the usage of the SDK:

#### [LollipopConsumerRequestConfig](core/src/main/java/it/pagopa/tech/lollipop/consumer/config/LollipopConsumerRequestConfig.java)

The baseline for the SDK configuration is in this configuration class. As for any of the config files within the core SDK
there is no reference to a particular method to be used for loading properties from an external source, leaving the possibility
and responsibility to implement a proper loading method in the service utilizing the library (See for example the 
[SpringLollipopConsumerRequestConfig](spring-impl/src/main/java/it/pagopa/tech/lollipop/consumer/spring/config/SpringLollipopConsumerRequestConfig.java)
 extension, using annotations to load properties from the application context).

Whenever is required to define a new instance of the configuration class programmatically, it may be used a builder
to setup the required parameters to be changed (not that otherwise the default values are applied):

```
lollipopConsumerRequestConfig = LollipopConsumerRequestConfig.builder()
        .assertionNotBeforeDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .assertionInstantDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .expectedFirstLcOriginalMethod('POST')
        .expectedFirstLcOriginalUrl('http://validurl:8080')
        .build();
```

Having defined the general configuration we can initialize the single instances of services (or related factories) to 
be used in the validation process.

#### [HttpMessageVerifierFactory](core/src/main/java/it/pagopa/tech/lollipop/consumer/http_verifier/HttpMessageVerifierFactory.java) 

An instance of the service to be used in order to verify the content digest and signatures related to the http request,
through an implementation of the [HttpMessageVerifier](core/src/main/java/it/pagopa/tech/lollipop/consumer/http_verifier/HttpMessageVerifier.java)
interface. The standard library does provide a factory interface to be used for the creation of a particular instance of the message verifier.

Whenever the factory helper class is not used and the generation of new instance of the service is required, we may use
the following to generate an instance using the factory interface:

```
HttpMessageVerifier verifier = httpMessageVerifierFactory.create();
```

The repository provides a standard implementation of the service, and related factory. In order to initialize the factory
the lollipopConsumerRequestConfig is required, as well as the value of the expected encoding. Note that the latter does
not have a specific configuration file:

```
HttpMessageVerifierFactory messageVerifierFactory = new VismaHttpMessageVerifierFactory("UTF-8", LollipopConsumerRequestConfig);
```

#### [LollipopConsumerRequestValidationService](core/src/main/java/it/pagopa/tech/lollipop/consumer/service/LollipopConsumerRequestValidationService.java)

An instance of this service is used to apply formal validation a LollipopConsumerRequest instance. In order to use
the provided implementation lollipopConsumerRequestConfig is required, as it is used to search for the headers using the 
name defined in the configuration class. We may define a new validation service as follows:

```
LollipopConsumerRequestValidationService lollipopConsumerRequestValidationService = new LollipopConsumerRequestValidationServiceImpl(lollipopConsumerRequestConfig);
```


#### [LollipopLoggerServiceFactory](core/src/main/java/it/pagopa/tech/lollipop/consumer/logger/LollipopLoggerServiceFactory.java)

An instance of the [LollipopLoggerService](core/src/main/java/it/pagopa/tech/lollipop/consumer/logger/LollipopLoggerService.java)
is used to apply a form of logging in pre-defined portions of the SDK core services, in order to audit request validations,
and if enabled the retrieval of Assertions and IdP Cert Data. The standard library does provide a factory interface to be used 
for the creation of a particular instance of the logger service.

Whenever the factory helper class is not used and the generation of new instance of the service is required, we may use
the following to generate an instance using the factory interface:

```
LollipopLoggerService lollipopLoggerService = lollipopLoggerServiceFactory.create();
```


The repository provides a standard implementation of the service, and related factory, using a standard logback implementation.
In order to initialize the factory use the following snippet:

```
LollipopLoggerServiceFactory lollipopLoggerServiceFactory = new LollipopLogbackLoggerServiceFactory();
```

The SDK provides a [default.xml](core/src/main/resources/it/pagopa/tech/lollipop/logging/logback/includes/defaults.xml)
logback include, as well as a default [logback.xml](core/src/main/resources/it/pagopa/tech/lollipop/logging/logback/logback.xml) file.
The default may be used as a portion of other logback configuration, as an example see the spring implementation
default [logback-spring.xml](spring-impl/src/main/resources/logback-spring.xml).

#### [AssertionStorageProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/storage/AssertionStorageProvider.java)

An instance of the service to be used in order to enable the possibility to retrieve or store already recovered
SAML Assertions from a cache or storage, through an implementation of the 
[AssertionStorage](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/storage/AssertionStorage.java)
interface. The standard library does provide a factory interface to be used for the creation of a particular instance of the storage.

Whenever the factory helper class is not used and the generation of new instance of the service is required, we may use
the following to generate an instance using the factory interface, using an instance of the StorageConfig class (described
in the configuration paragraph):

```
AssertionStorage assertionStorage = assertionStorageProvider.provideStorage(storageConfig);
```

The repository provides a standard implementation of the service, and related factory. that defines a simple in-memory cache
storage for limited use, in order to initialize it, it is required to use the 
[SimpleAssertionStorageProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/storage/SimpleAssertionStorageProvider.java). 

#### [IdpCertStorageProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/storage/IdpCertStorageProvider.java)

An instance of the service to be used in order to enable the possibility to retrieve or store already recovered
IdP Certificates from a cache or storage, through an implementation of the
[IdpCertStorage](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/storage/IdpCertStorage.java)
interface. The standard library does provide a factory interface to be used for the creation of a particular instance of the storage.

Whenever the factory helper class is not used and the generation of new instance of the service is required, we may use
the following to generate an instance using the factory interface, using an instance of the 
[IdpCertStorageConfig](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/storage/IdpCertStorageConfig.java) class
(described in the configuration paragraph):

```
IdpCertStorage idpCertStorage = idpCertStorageProvider.provideStorage(idpCertStorageConfig);
```

The repository provides a standard implementation of the service, and related factory. that defines a simple in-memory cache
storage for limited use, in order to initialize it, it is required to use the
[SimpleIdpCertStorageProvider](identity-service-rest-client-native/src/main/java/it/pagopa/tech/lollipop/consumer/idp/client/simple/storage/SimpleIdpCertStorageProvider.java).

Note that currently the storage is required only if applied directly to the default provider of IdP certificates data,
as it is directly used in the Identity Services implementation. In case other implementations are used, this storage
service may not be in use.

#### [AssertionClientProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/client/AssertionClientProvider.java)

An instance of the service to be used in order to enable the possibility to retrieve SAML Assertions through an implementation of the
[AssertionClient](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/client/AssertionClient.java)
interface. The standard library does provide a factory interface to be used for the creation of a particular instance of the client.

Whenever the factory helper class is not used and the generation of new instance of the service is required, we may use
the following to generate an instance using the factory interface:
```
AssertionClient assertionClient = assertionClientProvider.provideClient();
```

The repository provides a standard implementation of the service, and related factory, that defines a Rest Client using
the available functionalities in the JDK 11 environment, available in the [assertion-rest-client-native](assertion-rest-client-native) module.

See the related [README.md](assertion-rest-client-native/README.md) for further information about the usage of this implementation.

#### [IdpCertSimpleClientProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/client/IdpCertClientProvider.java)

An instance of the service to be used in order to enable the possibility to retrieve IdPCert data through an implementation of the
[IdpCertClient](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/client/IdpCertClient.java)
interface. The standard library does provide a factory interface to be used for the creation of a particular instance of the client.

Whenever the factory helper class is not used and the generation of new instance of the service is required, we may use
the following to generate an instance using the factory interface:
```
IdpCertClient idpCertClient = idpCertClientProvider.provideClient();
```

The repository provides a standard implementation of the service, and related factory, that defines a Rest Client using
the available functionalities in the JDK 11 environment, available in the
[identity-service-rest-client-native](identity-service-rest-client-native) module.

See the related [README.md](identity-service-rest-client-native/README.md) for further information about the usage of this implementation.


#### [IdpCertProviderFactory](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/IdpCertProviderFactory.java)

Once we have defined the necessary services to be used for the retrieval of the IdP certificates, we can use an implementation
of the [IdpCertProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/IdpCertProvider.java) interface, in 
order to provide a transparent functionality inside the SDK library. 

Whenever the factory helper class is not used and the generation of new instance of the service is required, we may use
the following to generate an instance using the factory interface:

```
IdpCertProvider idpCertProvider = idpCertProviderFactory.create();
```

The repository provides a standard implementation of the service, and related factory. In order to initialize the factory
an instance of the IdpCertClientProvider is required:

```
IdpCertProviderFactory idpCertProviderFactory = new IdpCertProviderFactoryImpl(idpCertClientProvider);
```

#### [AssertionServiceFactory](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/AssertionServiceFactory.java)

Once we have defined the necessary services to be used for the retrieval of the SAML Assertion data, we can use an implementation
of the [AssertionService](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/AssertionService.java) interface, in
order to provide a transparent functionality inside the SDK library.

Whenever the factory helper class is not used and the generation of new instance of the service is required, we may use
the following to generate an instance using the factory interface:

```
AssertionService assertionService = assertionServiceFactory.create();
```

The repository provides a standard implementation of the service, and related factory. In order to initialize the factory
an instance of the AssertionClientProvider, AssertionStorageProvider, and StorageConfig are required:

```
AssertionServiceFactory assertionServiceFactory = new AssertionServiceFactoryImpl(
                          assertionStorageProvider, assertionClientProvider, assertionStorageConfig);
```


#### [AssertionVerifierService](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/AssertionServiceFactory.java)

In order to execute a validation of the SAML Assertions starting form a LollipopConsumerRequest an interface of
the [AssertionVerifierService](core/src/main/java/it/pagopa/tech/lollipop/consumer/service/AssertionVerifierService.java)
has to be used.

The standard implementation of this interface expects to have instances of the previously described interfaces,
to be used for SAML Assertion retrieval and storage. Whenever we are using the factory helper it is not necessary
to directly manage in any part the service startup, as it will be managed by the helper class itsefl if an implementation
is missing.

In case it is necessary an instance of the standard implementation of the service can be applied with the following
snippet, provided that the required services have been produced by the respective factory methods (or directly):

```
AssertionServiceFactory assertionServiceFactory = new AssertionVerifierServiceImpl(
                                   lollipopLoggerService,
                                   idpCertProvider,
                                   assertionService,
                                   lollipopRequestConfig);
```

#### Factory Helper Startup

The LollipopFactoryHelper is a singleton class to be used by the LollipopCommandBuilder implementation, that provides
(and creates if unavailable) the required services, through the provided implementations of the factory classes for the
single components. It is not mandatory to be used in order to use the services, if the dependencies are injected using
a dedicated library. 

It requires an instance of a 
[LollipopConsumerRequestConfig](core/src/main/java/it/pagopa/tech/lollipop/consumer/config/LollipopConsumerRequestConfig.java),
[HttpMessageVerifierFactory](core/src/main/java/it/pagopa/tech/lollipop/consumer/http_verifier/HttpMessageVerifierFactory.java),
[AssertionStorageProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/storage/AssertionStorageProvider.java),
[IdpCertProviderFactory](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/IdpCertProviderFactory.java),
[AssertionServiceFactory](core/src/main/java/it/pagopa/tech/lollipop/consumer/assertion/AssertionServiceFactory.java),
[LollipopLoggerServiceFactory](core/src/main/java/it/pagopa/tech/lollipop/consumer/logger/LollipopLoggerServiceFactory.java).
[LollipopConsumerRequestValidationService](core/src/main/java/it/pagopa/tech/lollipop/consumer/service/LollipopConsumerRequestValidationService.java)

And related configs and sub-dependencies defined by the factory implementation.

```
LollipopConsumerFactoryHelper lollipopFactoryHelper = new LollipopConsumerFactoryHelper(lollipopLoggerServiceFactory,
                                                                      messageVerifierFactory, idpCertProviderFactory,
                                                                      assertionServiceFactory,
                                                                      lollipopConsumerRequestValidationService,
                                                                      lollipopConsumerRequestConfig);
```

#### Example

The following examples provides the complete setup method with default configurations created directly into the method.
For a simple usage of the library the following can be used with minimal setup changes in configuration related to the expect
origin method and url, and the rest client url to be used for Assertions retrieval.
In a normal usage case each factory implementation and related configurations should be treated with an implementation
for the related frameworks and libraries used for the application (As an example, defining Beans and configurations
within a Spring context, such as in the provided [spring-impl](spring-impl) module, and within the [spring sample](samples/spring)):

```
lollipopConsumerRequestConfig = LollipopConsumerRequestConfig.builder()
        .assertionNotBeforeDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .assertionInstantDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .build();
HttpMessageVerifierFactory messageVerifierFactory = new VismaHttpMessageVerifierFactory("UTF-8",
        LollipopConsumerRequestConfig.builder().build());
LollipopConsumerRequestValidationServiceImpl lollipopConsumerRequestValidationService = 
        new LollipopConsumerRequestValidationServiceImpl(lollipopConsumerRequestConfig);        
AssertionStorageProvider assertionStorageProvider = new SimpleAssertionStorageProvider();
IdpCertProviderFactory idpCertProviderFactory = new IdpCertProviderFactoryImpl(
        new IdpCertSimpleClientProvider(IdpCertSimpleClientConfig.builder().baseUri("http://localhost:3001").build(),
                new SimpleIdpCertStorageProvider(), new IdpCertStorageConfig()));
AssertionClientProvider assertionClientProvider =
        new AssertionSimpleClientProvider(AssertionSimpleClientConfig.builder().build());
AssertionServiceFactory assertionServiceFactory = new AssertionServiceFactoryImpl(
        assertionStorageProvider, assertionClientProvider, new StorageConfig());
LollipopLoggerServiceFactory lollipopLoggerServiceFactory = new LollipopLogbackLoggerServiceFactory();
LollipopConsumerFactoryHelper lollipopFactoryHelper = new LollipopConsumerFactoryHelper(lollipopLoggerServiceFactory,
                                         messageVerifierFactory, idpCertProviderFactory,
                                         assertionServiceFactory, lollipopConsumerRequestValidationService ,
                                         lollipopConsumerRequestConfig);
```

#### Execute Validation Command

In order to execute a validation of an instance of LollipopConsumerRequest, after an instance 
of a LollipopConsumerFactoryHelper we could create an instance of the LollipopCommandBuilder interface as in the following snippet,
directly into the execution method, or as the part of the application context (As an example, as a Spring Bean):

```
LollipopConsumerCommandBuilder commandBuilder = new LollipopConsumerCommandBuilderImpl(lollipopConsumerFactoryHelper);
```

With an existing builder we could provide a new instance of a command to execute using the following:

```
CommandResult result = commandBuilder.createCommand(lollipopConsumerRequest).doExecute();
```

A new LollipopConsumerCommand could be defined without the help of the FactoryHelper and the CommandBuilder, provided
instances of an implementation of the required services are available, directly in the class requiring the new instance,
or throughout the application context capable of enabling the usage of the _@Inject_ annotations within the implemented 
command. In the case of a direct creation of a command to execute, use the following:

```
LollipopConsumerCommand command = new LollipopConsumerCommandImpl(
                                    lollipopConsumerRequestConfig, httpMessageVerifierService,
                                    assertionVerifierService, lollipopConsumerRequestValidationService,
                                    lollipopLoggerService, lollipopConsumerRequest);
CommandResult commandResult = command.doExecute();                                    
```

#### Defining LollipopRequestCommand instance

In order to define an instance of the __LollipopRequestCommand__, to be used in the validation process it is required
to setup the following parameters:

- requestBody, contains the request body to be used for the digest validation
- headerParams, Map containing the list of parameters to be used within the validation process
- requestParams, Map containing the list of parameters to be used with the validation process 
(actually not in use, defined for possible use in other implementations)

The following snippet defines the conversion from an __HttpServletRequest__ instance, to a new __LollipopConsumerRequest__

```
LollipopConsumerRequest.builder()
  .requestBody(requestBody != null ? new String(requestBody) : null)
  .headerParams(
          Collections.list(httpServletRequest.getHeaderNames()).stream()
                  .collect(Collectors.toMap(h -> h, httpServletRequest::getHeader)))
  .requestParams(httpServletRequest.getParameterMap())
  .build();
```

#### Using CommandResult instances

Whenever a validation command is executed, a CommandResult will be produced, abstracting the exceptions or validation
errors produced by the services regarding the validation process for the http message, and the SAML assertion. 
The CommandResult has properties indicating:

- A result code to indicate if successful or not (in case of a valid request the SUCCESS code has to be expected)
- A message containing details on the result, usually to be used for errors

The following snippet shows an example of how the CommandResult could be used to define the HttpServletRespons status
and response body whenever the CommandResult is not ending in a successful state:

```
if (!commandResult.getResultCode().equals(VERIFICATION_SUCCESS_CODE)) {
    httpResponse.setStatus(401);
    httpResponse.getWriter().write(commandResult.getResultMessage());
}
```

## Utility classes

### LollipopConsumerConverter
Class that implements [Javax Servlet](https://mvnrepository.com/artifact/javax.servlet/javax.servlet-api/3.0.1) library
with two utility methods:
+ #### convertToLollipopRequest: 
  converts [HttpServletRequest](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html)
  to a [LollipopConsumerRequest](core/src/main/java/it/pagopa/tech/lollipop/consumer/model/LollipopConsumerRequest.java),
  class used in the verification process;
+ #### interceptResult:
  used to intercept the [HttpServletResponse](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletResponse.html)
  changing its status and response based on the verification results from the 
  [CommandResult](core/src/main/java/it/pagopa/tech/lollipop/consumer/model/CommandResult.java).

Examples of its usage can be found in the [Spring Interceptor](spring-impl/src/main/java/it/pagopa/tech/lollipop/consumer/spring/HttpVerifierHandlerInterceptor.java).

### LollipopTypesafeConfig
Class that implements [Lightbend's Config](https://github.com/lightbend/config) library, used to define configuration parameters.

The class' constructor retrieve from a [Config](https://lightbend.github.io/config/latest/api/com/typesafe/config/Config.html) 
object the parameters that can be used as configuration in the application, if the path to the parameter is not found 
(it hasn't been configured) a default value will be set.

Examples of its usage can be found in the [Simple Typesafe sample](samples/simpleTypesafe/src/main/java/it/pagopa/tech/sample/LollipopConsumerSample.java),
where the [Config](https://lightbend.github.io/config/latest/api/com/typesafe/config/Config.html) object is generated with
the library's class [ConfigFactory](https://lightbend.github.io/config/latest/api/com/typesafe/config/ConfigFactory.html)
using as source for the configuration the sample's [application.properties](samples/simpleTypesafe/src/main/resources/application.properties).


## Testing

Testing of the repository functionalities is conducted by unit test, integration and e2e testing.

### Collecting Test Coverage

This repository contains a module dedicated to collecting the test coverage of all the modules within the repository
through the usage of a dedicated gradle plugin.

In order to obtain a centralized report of the repository test coverage, the following command can be used on the
[test-coverage](test-coverage) module:


```
.\gradlew testCodeCoverageReport                                
```

Afterwards, the report is found at `/test-coverage/build/reports/jacoco/testCodeCoverageReport`.

### E2E

This repository contains a collection and docker-compose file to be used for the purpose of an integrated testing.
See the related [README.md](e2e/README.md) for further information.

## Generate new Rest Clients

The standard Rest Clients available in the repository are defined without a specific dependency, and relates
to the standard services available (For example, the IdP Data Provider is referred to the existing Pagopa Identity Services).

In case a new implementation of the client (for example, using Feign), or one of the clients has a different specification
from the one that has been used as a default, a tool may be used to generate the internal structure of the client using OpenApi
generation tool or gradle plugin. For the latter the following steps may be used to produce the internals of the Rest Client implementation:

Introduce in your module the following plugin:

`id("org.openapi.generator") version "6.5.0"`

Afterwards define the following task structure in your module:

```
compileJava.dependsOn tasks.openApiGenerate

openApiGenerate {
    generatorName = "java"
    inputSpec = "$projectDir/openapi/openapi-spec.yml"
    outputDir = "$projectDir/generated"
    configOptions = [
        dataLibrary: "java8",
        library: "native",
        useRuntimeException: "true",
        sourceFolder: "build/generated/sources/"
    ]
}
```
Where the `dataLibrary` config option defines the java version to be used, and the `library`
defines witch implementation to be used. (For example `library: "feign"` will generate a Feign Rest Client).

In order to execute the task run the following command:


`.\gradlew openApiGenerate`

The default openApi specs for the two services already implemented are found in the respective modules under the `openApi` folder.

See the [OpenAPITools/openapi-generator repository](https://github.com/OpenAPITools/openapi-generator) 
for further information about the generation toolkit.

Having the Rest Client code generated, the output should be introduced inside
the expected package within the module, and an instance of the core SDK interfaces should be
implemented in order to handle the data retrieval through the generated client.

For example, in order to enable the usage of a new Rest Client regarding the IdP certificates retrieval an implementation
of the [IdpCertClient](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/client/IdpCertClient.java) and 
[IdpCertClientProvider](core/src/main/java/it/pagopa/tech/lollipop/consumer/idp/client/IdpCertClientProvider.java)
should be defined. See for reference the 
[IdpCertSimpleClient](identity-service-rest-client-native/src/main/java/it/pagopa/tech/lollipop/consumer/idp/client/simple/IdpCertSimpleClient.java) implementation.