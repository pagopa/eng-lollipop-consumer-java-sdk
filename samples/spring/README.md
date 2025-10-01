# eng-lollipop-consumer-java-sdk

## Spring sample application

### Run the sample

+ #### Publish library dependencies to maven local
In the project root folder run

```bash
./gradlew publishToMavenLocal
```

+ #### Define environment variables for the sample
To run the application with the existing examples, define the following variables in the sample run configuration:

| VARIABLE                                  | VALUE                     |
|-------------------------------------------|---------------------------|
| ASSERTION_CLIENT_MOCK_ENABLED             | true                      |
| IDP_CLIENT_MOCK_ENABLED                   | true                      |
| IDP_CLIENT_BASE_URI                       | http://localhost:3001     |
| LOLLIPOP_ASSERTION_NOT_BEFORE_DATE_FORMAT | yyyy-MM-dd'T'HH:mm:ss.'Z' |
| LOLLIPOP_ASSERTION_INSTANT_DATE_FORMAT    | yyyy-MM-dd'T'HH:mm:ss.'Z' |

You can configure these variables for your custom usage (see "Configuration" paragraph)

+ #### Run the sample
You can now run the sample, it will be exposed to http://localhost:8080/api/v1/lollipop-consumer

To test the sample you can use our examples with these postman [environment](https://github.com/pagopa/eng-lollipop-consumer-java-sdk/blob/0f92a666b0f5e71ec13f11560e435be82df0f5e9/e2e/env/lollipopEnvironmentVariables.postman_environment.json)
and [collection](https://github.com/pagopa/eng-lollipop-consumer-java-sdk/blob/0f92a666b0f5e71ec13f11560e435be82df0f5e9/e2e/collections/lollipopSDKTest.postman_collection.json)

### Configuration
The configurable variables are the following:

| VARIABLE                                  | DEFAULT                                                               | USAGE                                                              |
|-------------------------------------------|-----------------------------------------------------------------------|--------------------------------------------------------------------|
| ASSERTION_CLIENT_MOCK_ENABLED             | false                                                                 | Enable Mockserver client                                           |
| IDP_CLIENT_MOCK_ENABLED                   | false                                                                 | Enable Mockserver client                                           |
| SAMPLE_LOLLIPOP_CONSUMER_ENDPOINT         | /api/v1/lollipop-consumer                                             | Define sample controller endpoint                                  |
| LOLLIPOP_ASSERTION_EXPIRE_IN_DAYS         | 180                                                                   | Define after how many days assertion expires                       |
| LOLLIPOP_EXPECTED_LC_ORIGINAL_URL         | https://api-app.io.pagopa.it/first-lollipop/sign                      | Define original url expected in request's header                   |
| LOLLIPOP_EXPECTED_LC_ORIGINAL_METHOD      | POST                                                                  | Define original method expected in request's header                |
| LOLLIPOP_ASSERTION_NOT_BEFORE_DATE_FORMAT | yyyy-MM-dd'T'HH:mm:ss.SSS'Z'                                          | Define the date format used in the Assertion's notBefore field     |
| LOLLIPOP_ASSERTION_INSTANT_DATE_FORMAT    | yyyy-MM-dd'T'HH:mm:ss.SSS'Z'                                          | Define the date format used in the Assertion's Issue Instant field |
| IDP_CLIENT_CIEID                          | https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO | Define entity id for CIE identity provider                         |
| IDP_CLIENT_BASE_URI                       | https://api.is.eng.pagopa.it                                          | Define base uri to retrieve IDP certification data                 |
| IDP_CLIENT_CIE_ENDPOINT                   | /idp-keys/cie                                                         | Define endpoint to IDP_CLIENT_BASE_URI for CIE's certification     |
| IDP_CLIENT_SPID_ENDPOINT                  | /idp-keys/spid                                                        | Define endpoint to IDP_CLIENT_BASE_URI for SPID's certification    |
| IDP_STORAGE_ENABLED                       | true                                                                  | Enable internal cache storage  for IDP certification data          |
| IDP_STORAGE_EVICTION_DELAY                | 1                                                                     | Define storage eviction delay for IDP's storage (in Minutes by default) |
| ASSERTION_REST_URI                        | http://localhost:3000                                                 | Define base uri to retrieve the Assertion                          |
| ASSERTION_REST_ENDPOINT                   | /assertions                                                           | Define endpoint to ASSERTION_REST_URI                              |
| ASSERTION_STORAGE_ENABLED                 | true                                                                  | Enable internal cache storage  for assertions                      |
| ASSERTION_STORAGE_EVICTION_DELAY          | 1                                                                     | Define storage eviction delay for assertion's storage (in Minutes by default) |