# eng-lollipop-consumer-java-sdk

## End-to-end test

### Testing models

+ #### Publish library dependencies to maven local
In the root folder run

```bash
./gradlew publishToMavenLocal
```

+ #### Build spring sample application
Change directory to ./samples/spring folder and build the sample with

```bash
cd samples/spring
./gradlew bootJar
```

+ #### Run docker container
Return to root folder and run docker compose in the e2e folder with

```bash
cd ..
cd e2e
docker compose --env-file .env.dev up --build
```

+ ##### Prepare newman
Staying in the e2e folder, install [newman](https://www.npmjs.com/package/newman) with npm

```bash
npm i newman
```

+ #### Run tests
Finally, when all docker's containers are healthy, you can run the tests with

```bash
npm run execute-test
```

+ #### View report
Newman generates a detailed report in the "newman" folder, you can open it with your preferred browser

### Configuration
The sample configuration can be changed with environment variables in the .env.dev files
(or using a different .env file in the docker compose command)

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
| IDP_STORAGE_EVICTION_DELAY                | 1                                                                     | Define storage eviction delay for IDP's storage                    |
| ASSERTION_REST_URI                        | http://localhost:3000                                                 | Define base uri to retrieve the Assertion                          |
| ASSERTION_REST_ENDPOINT                   | /assertions                                                           | Define endpoint to ASSERTION_REST_URI                              |
| ASSERTION_STORAGE_ENABLED                 | true                                                                  | Enable internal cache storage  for assertions                      |
| ASSERTION_STORAGE_ENABLED                 | 1                                                                     | Define storage eviction delay for assertion's storage              |