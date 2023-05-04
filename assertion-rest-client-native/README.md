# Assertion rest client
This module is used to obtain the user's SAML assertion.

The parameters needed to get the assertion are the jwt and the assertion's ref,
both retrieved from the http request's headers.

At this moment only SAML assertion are supported, OIDC claims are not.

## Configuration
The client uri, endpoints and the entity id of the CIE identity provider are configurable and are configured by default as follows:

| VARIABLE                 | DEFAULT VALUE         | USAGE                                             |
|--------------------------|-----------------------|---------------------------------------------------|
| baseUri                  | http://localhost:3000 | base uri of the api for retrieving the assertions |
| assertionRequestEndpoint | /assertions           | endpoint of the request                           |
