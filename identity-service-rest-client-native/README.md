# Identity service rest client
This module is used to obtain the identity provider certification data.

The parameters needed to find the right certificate are the entity id and the assertion's issue instant,
both retrieved from the assertion.

First we fetch the certificates' tag list (a list of certification by their issue instant) and we find the two tags
that could be used for the assertion verification; we compare the tags with the assertion's instant as timestamp in seconds and
the two eligible tags are the ones right before and after the provided assertion's instant.

Then for each of the found tags the corresponding certification as xml is fetched, 
the xml contains various elements called EntityDescriptor for each entity id,
so we will filter these elements by the entity id provided in first instance.

Finally, from the EntityDescriptor of the right identity provider we will extract the signature that will be used to 
verify the assertion.

## Configuration
The client uri, endpoints and the entity id of the CIE identity provider are configurable and are configured by default as follows:

| VARIABLE            | DEFAULT VALUE                                                         | USAGE                                                 |
|---------------------|-----------------------------------------------------------------------|-------------------------------------------------------|
| cieEntityId         | https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO | entity id of the CIE identity provider                |
| baseUri             | https://api.is.eng.pagopa.it                                          | base uri of the api for retrieving the certifications |
| idpKeysCieEndpoint  | /idp-keys/cie                                                         | endpoint for CIE certifications                       |
| idpKeysSpidEndpoint | /idp-keys/spid                                                        | endpoint for SPID certifications                      |

## Example

In order to create a new instance of the client using the provider and an instance of the configuration class. Note that
in order to use instances of a IdpCertStorageProvider and related configs (as described in the root Readme.md) should be
available for this implementation:

```
AssertionClientConfig config = AssertionSimpleClientConfig.builder().build();
IdpCertSimpleClientProvider idpCertSimpleClientProvider =
        new IdpCertSimpleClientProvider(config, idpCertStorageProvider, idpCertStorageConfig);
```